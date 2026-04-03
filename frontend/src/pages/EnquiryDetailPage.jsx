import { useEffect, useMemo, useState } from "react"
import { useNavigate, useParams } from "react-router-dom"
import {
    ArrowLeft,
    Briefcase,
    Building,
    Calendar,
    Edit,
    FileText,
    Loader2,
    Mail,
    MapPin,
    MessageSquare,
    Phone,
    Plus,
} from "lucide-react"

import { AppLayout } from "../components/layout/AppLayout"
import { Badge } from "../components/ui/Badge"
import { Button } from "../components/ui/Button"
import { Card } from "../components/ui/Card"
import { FormInput } from "../components/ui/FormInput"
import { FormSelect } from "../components/ui/FormSelect"
import { FormTextarea } from "../components/ui/FormTextarea"
import { Modal, TwoColumnModal, ModalSection } from "../components/ui/Modal"
import { SkeletonLoader } from "../components/ui/SkeletonLoader"
import { Timeline } from "../components/ui/Timeline"
import { useToast } from "../components/ui/Toast"
import { enquiryService } from "../services/enquiryService"
import { followUpService } from "../services/followUpService"
import { FOLLOWUP_EVENT_TAGS } from "../utils/constants"
import { formatCurrency, formatDate, formatDateTime, isWithinHours, validateEmail, validatePhone } from "../utils/helpers"

const buildEditForm = (enquiry) => ({
    leadName: enquiry?.leadName || "",
    leadMobileNumber: enquiry?.leadMobileNumber || "",
    leadLandlineNumber: enquiry?.leadLandlineNumber || "",
    leadEmail: enquiry?.leadEmail || "",
    leadCity: enquiry?.leadCity || "",
    leadAddress: enquiry?.leadAddress || "",
    leadOccupation: enquiry?.leadOccupation || "",
    leadCompany: enquiry?.leadCompany || "",
    propertyType: enquiry?.propertyType || "",
    property: enquiry?.property || "",
    area: enquiry?.area == null ? "" : String(enquiry.area),
    budget: enquiry?.budget || "",
    reference: enquiry?.reference || "",
    referenceName: enquiry?.referenceName || "",
    status: enquiry?.status || "ONGOING",
    remark: enquiry?.remark || "",
})

const getDefaultFollowUpDate = () => {
    const d = new Date()
    d.setDate(d.getDate() + 5)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day}T${hours}:${minutes}`
}

const buildInitialNoteForm = () => ({
    body: "",
    tag: FOLLOWUP_EVENT_TAGS.CLIENT_CALLED,
    followUpNextDate: getDefaultFollowUpDate(),
})

export default function EnquiryDetailPage() {
    const { enquiryId } = useParams()
    const navigate = useNavigate()
    const { success, error } = useToast()

    const [enquiry, setEnquiry] = useState(null)
    const [followUp, setFollowUp] = useState(null)
    const [propertyOptions, setPropertyOptions] = useState(null)
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [addingNote, setAddingNote] = useState(false)
    const [timelineLoading, setTimelineLoading] = useState(false)
    const [timelineError, setTimelineError] = useState("")
    const [editOpen, setEditOpen] = useState(false)
    const [timelineOpen, setTimelineOpen] = useState(false)
    const [editForm, setEditForm] = useState(buildEditForm(null))
    const [noteForm, setNoteForm] = useState(buildInitialNoteForm())
    const [editingNode, setEditingNode] = useState(null)

    useEffect(() => {
        const fetchEnquiry = async () => {
            try {
                setLoading(true)

                const enquiryResponse = await enquiryService.getEnquiry(enquiryId)
                setEnquiry(enquiryResponse)
                setEditForm(buildEditForm(enquiryResponse))

                if (enquiryResponse.projectId) {
                    try {
                        const options = await enquiryService.getPropertyOptions(enquiryResponse.projectId)
                        setPropertyOptions(options)
                    } catch {
                        setPropertyOptions(null)
                    }
                } else {
                    setPropertyOptions(null)
                }
            } catch (err) {
                console.error("Failed to load enquiry:", err)
                error(err.message || "Failed to load enquiry")
            } finally {
                setLoading(false)
            }
        }

        if (enquiryId) {
            fetchEnquiry()
        }
    }, [enquiryId, error])

    const propertyTypes = useMemo(() => {
        return (propertyOptions?.propertyTypes || []).map((item) => ({
            value: item.propertyType,
            label: item.propertyType,
        }))
    }, [propertyOptions])

    const properties = useMemo(() => {
        const selectedType = (propertyOptions?.propertyTypes || []).find(
            (item) => item.propertyType === editForm.propertyType,
        )

        return (selectedType?.properties || []).map((item) => ({
            value: item.property,
            label: item.property || "Standard Unit",
        }))
    }, [propertyOptions, editForm.propertyType])

    const areas = useMemo(() => {
        const selectedType = (propertyOptions?.propertyTypes || []).find(
            (item) => item.propertyType === editForm.propertyType,
        )
        const selectedProperty = (selectedType?.properties || []).find(
            (item) => item.property === editForm.property,
        )

        return (selectedProperty?.areas || []).map((item) => ({
            value: String(item.area),
            label: `${item.area} sq ft (${item.propertiesAvailable} avail)`,
        }))
    }, [propertyOptions, editForm.propertyType, editForm.property])

    const timelineEvents = useMemo(() => {
        if (!followUp?.followUpNodes) {
            return []
        }

        const orderedNodes = [...followUp.followUpNodes]
            .sort((a, b) => new Date(b.followUpDateTime) - new Date(a.followUpDateTime))
        const latestNodeId = orderedNodes[0]?.followUpNodeId

        return orderedNodes.map((node) => ({
            id: node.followUpNodeId,
            title: node.tag || "Update",
            timestamp: formatDateTime(node.followUpDateTime),
            groupDate: new Date(node.followUpDateTime).toLocaleDateString("en-GB", {
                day: "2-digit",
                month: "short",
                year: "numeric",
            }).toUpperCase(),
            description: node.body,
            agent: node.agentName || "System",
            createdAt: node.createdAt,
            canEdit: node.followUpNodeId === latestNodeId && isWithinHours(node.createdAt, 24),
        }))
    }, [followUp])

    const noteEditingBlocked =
        !followUp?.followUpId ||
        Boolean(timelineError) ||
        timelineLoading ||
        enquiry?.status === "BOOKED" ||
        enquiry?.status === "CANCELLED"

    const refreshEnquiry = async () => {
        const enquiryResponse = await enquiryService.getEnquiry(enquiryId)
        setEnquiry(enquiryResponse)
        setEditForm(buildEditForm(enquiryResponse))
        return enquiryResponse
    }

    const refreshFollowUp = async () => {
        const response = await followUpService.getEnquiryFollowUps(enquiryId)
        setFollowUp(response)
        setTimelineError("")
        return response
    }

    const openTimelineModal = async () => {
        setTimelineOpen(true)
        setTimelineLoading(true)
        setTimelineError("")
        setEditingNode(null)

        try {
            const response = await refreshFollowUp()
            setNoteForm(buildInitialNoteForm())
        } catch (err) {
            console.error("Failed to load follow-up timeline:", err)
            setFollowUp(null)
            setTimelineError(err.message || "Failed to load follow-up timeline")
        } finally {
            setTimelineLoading(false)
        }
    }

    const handleSaveEnquiry = async () => {
        if (!editForm.leadName || !validatePhone(editForm.leadMobileNumber) || !validateEmail(editForm.leadEmail)) {
            error("Please provide a valid lead name, mobile number, and email")
            return
        }

        try {
            setSaving(true)
            await enquiryService.updateEnquiry(enquiryId, {
                ...editForm,
                area: editForm.area === "" ? null : Number(editForm.area),
            })
            await refreshEnquiry()
            success("Enquiry updated successfully")
            setEditOpen(false)
        } catch (err) {
            console.error("Failed to update enquiry:", err)
            error(err.message || "Failed to update enquiry")
        } finally {
            setSaving(false)
        }
    }

    const resetNoteForm = () => {
        setEditingNode(null)
        setNoteForm(buildInitialNoteForm())
    }

    const handleEditTimelineNode = (event) => {
        if (!followUp?.followUpNodes || !event?.id) return

        const node = followUp.followUpNodes.find((item) => item.followUpNodeId === event.id)
        if (!node) return

        setEditingNode(node)
        setNoteForm({
            body: node.body || "",
            tag: node.tag || FOLLOWUP_EVENT_TAGS.CLIENT_CALLED,
            followUpNextDate: getDefaultFollowUpDate(),
        })
    }

    const handleAddNote = async () => {
        if (!followUp?.followUpId) {
            error("No follow-up thread found for this enquiry")
            return
        }

        if (enquiry?.status === "BOOKED" || enquiry?.status === "CANCELLED") {
            error("Follow-up notes are disabled for booked or cancelled enquiries")
            return
        }

        if (!noteForm.body.trim() || !noteForm.followUpNextDate) {
            error("Please enter a follow-up note and next follow-up date")
            return
        }

        try {
            setAddingNote(true)
            if (editingNode) {
                await followUpService.updateFollowUpNode(followUp.followUpId, editingNode.followUpNodeId, noteForm)
                success("Follow-up note updated successfully")
            } else {
                await followUpService.addFollowUpNode(followUp.followUpId, noteForm)
                success("Follow-up note added successfully")
            }
            await refreshFollowUp()
            setNoteForm(buildInitialNoteForm())
            setEditingNode(null)
        } catch (err) {
            console.error("Failed to add follow-up note:", err)
            error(err.message || "Failed to save follow-up note")
        } finally {
            setAddingNote(false)
        }
    }

    if (loading) {
        return (
            <AppLayout>
                <SkeletonLoader type="cards" count={3} />
            </AppLayout>
        )
    }

    if (!enquiry) {
        return (
            <AppLayout>
                <Card>
                    <p className="text-sm text-gray-600">Enquiry not found.</p>
                </Card>
            </AppLayout>
        )
    }

    return (
        <AppLayout>
            <div className="space-y-6">
                <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                    <div className="flex items-start gap-3">
                        <Button variant="outline" onClick={() => navigate("/enquiry-book")} className="mt-1">
                            <ArrowLeft size={16} />
                            Back
                        </Button>
                        <div>
                            <div className="flex items-center gap-3">
                                <h1 className="text-2xl md:text-3xl font-bold text-gray-900">{enquiry.leadName}</h1>
                                <Badge status={enquiry.status}>{enquiry.status}</Badge>
                            </div>
                            <p className="mt-1 text-sm md:text-base text-gray-600">
                                {enquiry.projectName} · {enquiry.leadMobileNumber || "No mobile"} · {enquiry.leadEmail || "No email"}
                            </p>
                        </div>
                    </div>

                    <div className="flex flex-wrap gap-3">
                        <Button variant="outline" onClick={() => setEditOpen(true)}>
                            <Edit size={16} />
                            Edit Enquiry
                        </Button>
                        <Button onClick={openTimelineModal}>
                            <MessageSquare size={16} />
                            View Timeline
                        </Button>
                    </div>
                </div>

                <div className="grid gap-6 lg:grid-cols-[1.1fr,0.9fr]">
                    <Card className="space-y-5">
                        <div>
                            <h2 className="text-lg font-semibold text-gray-900">Lead Details</h2>
                            <p className="mt-1 text-sm text-gray-500">Enquiry-owned person details used until a booking is created.</p>
                        </div>

                        <div className="grid gap-4 sm:grid-cols-2">
                            <InfoRow icon={Phone} label="Mobile" value={enquiry.leadMobileNumber} />
                            <InfoRow icon={Phone} label="Landline" value={enquiry.leadLandlineNumber || "Not provided"} />
                            <InfoRow icon={Mail} label="Email" value={enquiry.leadEmail} />
                            <InfoRow icon={MapPin} label="City" value={enquiry.leadCity} />
                            <InfoRow icon={Briefcase} label="Occupation" value={enquiry.leadOccupation} />
                            <InfoRow icon={Building} label="Company" value={enquiry.leadCompany} />
                        </div>

                        <div className="rounded-2xl border border-gray-200 bg-gray-50 p-4">
                            <p className="text-xs font-semibold uppercase tracking-wide text-gray-500">Address</p>
                            <p className="mt-2 text-sm text-gray-700">{enquiry.leadAddress}</p>
                        </div>
                    </Card>

                    <Card className="space-y-5">
                        <div>
                            <h2 className="text-lg font-semibold text-gray-900">Enquiry Snapshot</h2>
                            <p className="mt-1 text-sm text-gray-500">Current buying intent and property preference.</p>
                        </div>

                        <div className="grid gap-4 sm:grid-cols-2">
                            <InfoRow icon={Building} label="Project" value={enquiry.projectName} />
                            <InfoRow icon={FileText} label="Property Type" value={enquiry.propertyType || "Not selected"} />
                            <InfoRow icon={FileText} label="Property" value={enquiry.property || "Not selected"} />
                            <InfoRow icon={FileText} label="Area" value={enquiry.area == null ? "Not selected" : `${enquiry.area} sq ft`} />
                            <InfoRow icon={Calendar} label="Budget" value={formatCurrency(Number(enquiry.budget || 0))} />
                            <InfoRow icon={FileText} label="Reference" value={enquiry.reference || "Not provided"} />
                        </div>

                        <div className="rounded-2xl border border-gray-200 bg-gray-50 p-4">
                            <p className="text-xs font-semibold uppercase tracking-wide text-gray-500">Reference Name</p>
                            <p className="mt-2 text-sm text-gray-700">{enquiry.referenceName || "Not provided"}</p>
                        </div>

                        <div className="rounded-2xl border border-gray-200 bg-gray-50 p-4">
                            <p className="text-xs font-semibold uppercase tracking-wide text-gray-500">Remark</p>
                            <p className="mt-2 text-sm text-gray-700">{enquiry.remark || "No remark added"}</p>
                        </div>
                    </Card>
                </div>

            </div>

            <Modal
                isOpen={editOpen}
                onClose={() => setEditOpen(false)}
                title="Edit Enquiry"
                size="4xl"
                variant="form"
                footer={
                    <div className="flex justify-end gap-3">
                        <Button variant="outline" onClick={() => setEditOpen(false)}>
                            Cancel
                        </Button>
                        <Button onClick={handleSaveEnquiry} loading={saving}>
                            Save Changes
                        </Button>
                    </div>
                }
            >
                <div className="grid gap-6 lg:grid-cols-2">
                    <div>
                        <h3 className="mb-4 text-base font-semibold text-gray-900">Lead Information</h3>
                        <FormInput
                            label="Lead Name"
                            value={editForm.leadName}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, leadName: e.target.value }))}
                            required
                        />
                        <FormInput
                            label="Mobile Number"
                            value={editForm.leadMobileNumber}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, leadMobileNumber: e.target.value.replace(/\D/g, "").slice(0, 10) }))}
                            required
                        />
                        <FormInput
                            label="Landline Number"
                            value={editForm.leadLandlineNumber}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, leadLandlineNumber: e.target.value.replace(/\D/g, "").slice(0, 10) }))}
                        />
                        <FormInput
                            label="Email"
                            type="email"
                            value={editForm.leadEmail}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, leadEmail: e.target.value }))}
                            required
                        />
                        <FormInput
                            label="City"
                            value={editForm.leadCity}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, leadCity: e.target.value }))}
                            required
                        />
                        <FormInput
                            label="Occupation"
                            value={editForm.leadOccupation}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, leadOccupation: e.target.value }))}
                            required
                        />
                        <FormInput
                            label="Company"
                            value={editForm.leadCompany}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, leadCompany: e.target.value }))}
                            required
                        />
                        <FormTextarea
                            label="Address"
                            value={editForm.leadAddress}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, leadAddress: e.target.value }))}
                            rows={4}
                            required
                        />
                    </div>

                    <div>
                        <h3 className="mb-4 text-base font-semibold text-gray-900">Enquiry Information</h3>
                        <FormSelect
                            label="Property Type"
                            value={editForm.propertyType}
                            onChange={(e) =>
                                setEditForm((prev) => ({
                                    ...prev,
                                    propertyType: e.target.value,
                                    property: "",
                                    area: "",
                                }))
                            }
                            options={propertyTypes}
                            placeholder="Select property type"
                        />
                        <FormSelect
                            label="Property"
                            value={editForm.property}
                            onChange={(e) =>
                                setEditForm((prev) => ({
                                    ...prev,
                                    property: e.target.value,
                                    area: "",
                                }))
                            }
                            options={properties}
                            placeholder="Select property"
                            disabled={!editForm.propertyType}
                        />
                        <FormSelect
                            label="Area"
                            value={editForm.area}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, area: e.target.value }))}
                            options={areas}
                            placeholder="Select area"
                            disabled={!editForm.property}
                        />
                        <FormInput
                            label="Budget"
                            value={editForm.budget}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, budget: e.target.value }))}
                            required
                        />
                        <FormSelect
                            label="Status"
                            value={editForm.status}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, status: e.target.value }))}
                            options={[
                                { value: "ONGOING", label: "Ongoing" },
                                { value: "HOT_LEAD", label: "Hot Lead" },
                                { value: "WARM_LEAD", label: "Warm Lead" },
                                { value: "COLD_LEAD", label: "Cold Lead" },
                                { value: "BOOKED", label: "Booked" },
                                { value: "CANCELLED", label: "Cancelled" },
                            ]}
                        />
                        <FormInput
                            label="Reference"
                            value={editForm.reference}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, reference: e.target.value }))}
                        />
                        <FormInput
                            label="Reference Name"
                            value={editForm.referenceName}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, referenceName: e.target.value }))}
                        />
                        <FormTextarea
                            label="Remark"
                            value={editForm.remark}
                            onChange={(e) => setEditForm((prev) => ({ ...prev, remark: e.target.value }))}
                            rows={4}
                        />
                    </div>
                </div>
            </Modal>

            {/* Timeline Modal - Same as FollowUp Page */}
            <TwoColumnModal
                isOpen={timelineOpen}
                onClose={() => {
                    if (addingNote) return
                    setTimelineOpen(false)
                    setEditingNode(null)
                    setNoteForm(buildInitialNoteForm())
                }}
                title="Follow-Up Timeline"
                leftContent={
                    <div className="flex flex-col h-full">

                        <div className="mb-6 bg-gray-50 p-4 rounded-xl border border-gray-100 flex items-center justify-between">
                            <p className="text-md font-bold text-gray-900">{enquiry.leadName}</p>
                            <div className="flex items-center gap-2">
                                <Phone size={14} className="text-gray-400" />
                                <span className="text-sm font-medium text-gray-700">{enquiry.leadMobileNumber}</span>
                            </div>
                        </div>

                        <div className="flex-1 overflow-y-auto pr-2 max-h-[calc(90vh-350px)]">
                            <div className="flex items-center justify-between mb-4 sticky top-0 bg-white z-20 py-2">
                                <div className="flex items-center gap-2">
                                    <Calendar size={20} className="text-purple-600" />
                                    <h3 className="font-semibold text-gray-900">Activity Timeline</h3>
                                </div>
                                <button
                                    onClick={() => {
                                        const el = document.getElementById("detail-add-note-section")
                                        if (el) el.scrollIntoView({ behavior: "smooth" })
                                    }}
                                    className="md:hidden flex items-center justify-center w-8 h-8 rounded-full bg-indigo-600 text-white shadow-lg hover:bg-indigo-700 transition-colors"
                                    aria-label="Add Note"
                                >
                                    <Plus size={18} />
                                </button>
                            </div>

                            {timelineLoading ? (
                                <div className="flex min-h-56 items-center justify-center rounded-2xl border border-dashed border-gray-300 bg-gray-50">
                                    <div className="flex items-center gap-2 text-sm text-gray-500">
                                        <Loader2 size={16} className="animate-spin" />
                                        Loading timeline
                                    </div>
                                </div>
                            ) : timelineError ? (
                                <div className="rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
                                    {timelineError}
                                </div>
                            ) : timelineEvents.length > 0 ? (
                                <Timeline events={timelineEvents} onEditEvent={handleEditTimelineNode} />
                            ) : (
                                <div className="rounded-2xl border border-dashed border-gray-300 p-6 text-sm text-gray-500">
                                    No follow-up activity recorded yet.
                                </div>
                            )}
                        </div>
                    </div>
                }
                rightContent={
                    <div className="space-y-6 px-2 overflow-y-auto max-h-[calc(90vh-200px)]">
                        <ModalSection title="Follow-Up Details" icon={Calendar} iconColor="text-indigo-600">
                            <div className="space-y-4">
                                <div>
                                    <p className="text-sm md:text-md text-gray-600 mb-1">Follow-Up Date</p>
                                    <p className="text-sm md:text-base font-semibold text-gray-900">
                                        {followUp?.followUpNextDate ? formatDate(followUp.followUpNextDate) : "Not scheduled"}
                                    </p>
                                </div>
                            </div>
                        </ModalSection>

                        <div id="detail-add-note-section">
                            <ModalSection
                                title={editingNode ? "Edit Note" : "Add New Note"}
                                icon={FileText}
                                iconColor="text-green-600"
                            >
                                <div className="space-y-3">
                                    {enquiry.status === "BOOKED" || enquiry.status === "CANCELLED" ? (
                                        <div className="mb-4 rounded-2xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-800">
                                            Follow-up notes are disabled for booked or cancelled enquiries.
                                        </div>
                                    ) : timelineError ? (
                                        <div className="mb-4 rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
                                            Resolve the timeline load issue before adding a note.
                                        </div>
                                    ) : !timelineLoading && !followUp?.followUpId ? (
                                        <div className="mb-4 rounded-2xl border border-gray-200 bg-gray-50 p-4 text-sm text-gray-600">
                                            No follow-up thread is available for this enquiry.
                                        </div>
                                    ) : null}

                                    <FormInput
                                        value={noteForm.body}
                                        onChange={(e) => setNoteForm((prev) => ({ ...prev, body: e.target.value }))}
                                        placeholder="Enter your note..."
                                        disabled={noteEditingBlocked}
                                    />
                                    <FormSelect
                                        label="Event Tag"
                                        value={noteForm.tag}
                                        onChange={(e) => setNoteForm((prev) => ({ ...prev, tag: e.target.value }))}
                                        options={Object.entries(FOLLOWUP_EVENT_TAGS).map(([key, value]) => ({
                                            value: value,
                                            label: value,
                                        }))}
                                        disabled={noteEditingBlocked}
                                        required
                                    />
                                    <FormInput
                                        type="datetime-local"
                                        label="Next Follow Up Date (optional)"
                                        value={noteForm.followUpNextDate}
                                        onChange={(e) => setNoteForm((prev) => ({ ...prev, followUpNextDate: e.target.value }))}
                                        disabled={noteEditingBlocked}
                                    />

                                    <Button onClick={handleAddNote} variant="primary" className="w-full" loading={addingNote} disabled={noteEditingBlocked}>
                                        {editingNode ? "Save Changes" : "Add Note"}
                                    </Button>
                                    {editingNode && (
                                        <Button variant="secondary" className="w-full" onClick={resetNoteForm} disabled={addingNote}>
                                            Cancel Edit
                                        </Button>
                                    )}
                                </div>
                            </ModalSection>
                        </div>
                    </div>
                }
                columnGap="lg"
            />
        </AppLayout>
    )
}

function InfoRow({ icon: Icon, label, value }) {
    return (
        <div className="rounded-2xl border border-gray-200 p-4">
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-gray-500">
                <Icon size={14} />
                {label}
            </div>
            <p className="mt-3 text-sm font-medium text-gray-900">{value}</p>
        </div>
    )
}
