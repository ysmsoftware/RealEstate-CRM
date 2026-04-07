import { useEffect, useMemo, useState, useCallback } from "react"
import { Loader2, Plus, Search, Calendar, Phone, FileText, MessageSquare } from "lucide-react"
import { useNavigate } from "react-router-dom"

import { AppLayout } from "../components/layout/AppLayout"
import { Badge } from "../components/ui/Badge"
import { Button } from "../components/ui/Button"
import { Card } from "../components/ui/Card"
import { FormInput } from "../components/ui/FormInput"
import { FormSelect } from "../components/ui/FormSelect"
import { FormTextarea } from "../components/ui/FormTextarea"
import { Modal, TwoColumnModal, ModalSection } from "../components/ui/Modal"
import { SkeletonLoader } from "../components/ui/SkeletonLoader"
import { Table } from "../components/ui/Table"
import { Timeline } from "../components/ui/Timeline"
import { useToast } from "../components/ui/Toast"
import { enquiryService } from "../services/enquiryService"
import { followUpService } from "../services/followUpService"
import { projectService } from "../services/projectService"
import { FOLLOWUP_EVENT_TAGS } from "../utils/constants"
import { useQueryClient } from "@tanstack/react-query"
import { useEnquiries, useCreateEnquiry, useUpdateEnquiry } from "../api/hooks/useEnquiries"
import { useProjects } from "../api/hooks/useProjects"
import { formatCurrency, formatDate, formatDateTime, isWithinHours, validateEmail, validatePhone, getDefaultFollowUpDate } from "../utils/helpers"

const buildInitialForm = () => ({
    leadName: "",
    leadMobileNumber: "",
    leadLandlineNumber: "",
    leadEmail: "",
    leadCity: "",
    leadAddress: "",
    leadOccupation: "",
    leadCompany: "",
    projectId: "",
    propertyType: "",
    property: "",
    area: "",
    budget: "",
    reference: "",
    referenceName: "",
    remark: "",
    status: "ONGOING",
})

const buildUpdatePayload = (form) => ({
    leadName: form.leadName,
    leadMobileNumber: form.leadMobileNumber,
    leadLandlineNumber: form.leadLandlineNumber || null,
    leadEmail: form.leadEmail,
    leadCity: form.leadCity,
    leadAddress: form.leadAddress,
    leadOccupation: form.leadOccupation,
    leadCompany: form.leadCompany,
    propertyType: form.propertyType || null,
    property: form.property || null,
    area: form.area === "" ? null : Number(form.area),
    budget: form.budget,
    reference: form.reference,
    referenceName: form.referenceName,
    remark: form.remark || null,
    status: form.status,
})

const buildCreatePayload = (form) => ({
    leadName: form.leadName,
    leadMobileNumber: form.leadMobileNumber,
    leadLandlineNumber: form.leadLandlineNumber || null,
    leadEmail: form.leadEmail,
    leadCity: form.leadCity,
    leadAddress: form.leadAddress,
    leadOccupation: form.leadOccupation,
    leadCompany: form.leadCompany,
    projectId: form.projectId,
    propertyType: form.propertyType,
    property: form.property,
    area: Number(form.area),
    budget: form.budget,
    reference: form.reference,
    referenceName: form.referenceName,
})

export default function EnquiryBookPage() {
    const navigate = useNavigate()
    const { success, error } = useToast()

    const queryClient = useQueryClient()
    const [propertyOptions, setPropertyOptions] = useState(null)
    const [showModal, setShowModal] = useState(false)
    const [searchTerm, setSearchTerm] = useState("")
    const [filters, setFilters] = useState({ projectId: "", status: "" })
    const [editingEnquiry, setEditingEnquiry] = useState(null)
    const [form, setForm] = useState(buildInitialForm())

    // Timeline modal state
    const [showTimelineModal, setShowTimelineModal] = useState(false)
    const [selectedEnquiry, setSelectedEnquiry] = useState(null)
    const [followUp, setFollowUp] = useState(null)
    const [timelineLoading, setTimelineLoading] = useState(false)
    const [timelineError, setTimelineError] = useState("")
    const [addingNote, setAddingNote] = useState(false)
    const [nodeForm, setNodeForm] = useState({
        body: "",
        tag: FOLLOWUP_EVENT_TAGS.CLIENT_CALLED,
        followUpNextDate: getDefaultFollowUpDate(),
    })
    const [editingNode, setEditingNode] = useState(null)

    const { data: enquiries = [], isLoading: loading } = useEnquiries()

    const { data: projects = [] } = useProjects()

    const createMutation = useCreateEnquiry()
    const updateMutation = useUpdateEnquiry()

    const filteredEnquiries = useMemo(() => {
        return enquiries.filter((item) => {
            const matchesProject = !filters.projectId || item.projectId === filters.projectId
            const matchesStatus = !filters.status || item.status === filters.status
            const haystack = [
                item.leadName,
                item.leadMobileNumber,
                item.leadEmail,
                item.projectName,
                item.reference,
                item.referenceName,
            ]
                .filter(Boolean)
                .join(" ")
                .toLowerCase()
            const matchesSearch =
                !searchTerm ||
                haystack.includes(searchTerm.toLowerCase()) ||
                String(item.budget || "").includes(searchTerm)

            return matchesProject && matchesStatus && matchesSearch
        })
    }, [enquiries, filters, searchTerm])

    // Timeline events
    const timelineEvents = useMemo(() => {
        if (!followUp?.followUpNodes) return []

        const orderedNodes = [...followUp.followUpNodes]
            .sort((a, b) => new Date(b.followUpDateTime) - new Date(a.followUpDateTime))
        const latestNodeId = orderedNodes[0]?.followUpNodeId

        return orderedNodes.map((node) => ({
            id: node.followUpNodeId,
            title: node.tag || "Note Added",
            timestamp: formatDateTime(node.followUpDateTime),
            groupDate: new Date(node.followUpDateTime).toLocaleDateString("en-GB", {
                day: "numeric",
                month: "short",
                year: "numeric",
            }),
            description: node.body,
            agent: node.agentName,
            createdAt: node.createdAt,
            canEdit: node.followUpNodeId === latestNodeId && isWithinHours(node.createdAt, 24),
            rawDate: new Date(node.followUpDateTime),
        }))
    }, [followUp])

    const noteEditingBlocked =
        !followUp?.followUpId ||
        Boolean(timelineError) ||
        timelineLoading ||
        selectedEnquiry?.status === "BOOKED" ||
        selectedEnquiry?.status === "CANCELLED"

    const propertyTypes = useMemo(() => {
        return (propertyOptions?.propertyTypes || []).map((item) => ({
            value: item.propertyType,
            label: item.propertyType,
        }))
    }, [propertyOptions])

    const properties = useMemo(() => {
        const selectedType = (propertyOptions?.propertyTypes || []).find(
            (item) => item.propertyType === form.propertyType,
        )

        return (selectedType?.properties || []).map((item) => ({
            value: item.property,
            label: item.property || "Standard Unit",
        }))
    }, [propertyOptions, form.propertyType])

    const areas = useMemo(() => {
        const selectedType = (propertyOptions?.propertyTypes || []).find(
            (item) => item.propertyType === form.propertyType,
        )
        const selectedProperty = (selectedType?.properties || []).find(
            (item) => item.property === form.property,
        )

        return (selectedProperty?.areas || []).map((item) => ({
            value: String(item.area),
            label: `${item.area} sq ft (${item.propertiesAvailable} avail)`,
        }))
    }, [propertyOptions, form.propertyType, form.property])

    const openCreateModal = () => {
        setEditingEnquiry(null)
        setForm(buildInitialForm())
        setPropertyOptions(null)
        setShowModal(true)
    }

    const openEditModal = async (enquiry) => {
        setEditingEnquiry(enquiry)
        setForm({
            ...buildInitialForm(),
            ...enquiry,
            area: enquiry.area == null ? "" : String(enquiry.area),
        })
        setShowModal(true)

        if (enquiry.projectId) {
            try {
                const options = await enquiryService.getPropertyOptions(enquiry.projectId)
                setPropertyOptions(options)
            } catch (err) {
                console.error("Failed to load property options:", err)
                error("Failed to load property options")
            }
        }
    }

    const handleProjectChange = async (projectId) => {
        setForm((prev) => ({
            ...prev,
            projectId,
            propertyType: "",
            property: "",
            area: "",
        }))

        if (!projectId) {
            setPropertyOptions(null)
            return
        }

        try {
            const options = await enquiryService.getPropertyOptions(projectId)
            setPropertyOptions(options)
        } catch (err) {
            console.error("Failed to load property options:", err)
            error("Failed to load property options")
        }
    }

    // Timeline modal handlers
    const handleViewTimeline = useCallback(async (enquiry) => {
        setSelectedEnquiry(enquiry)
        setShowTimelineModal(true)
        setTimelineLoading(true)
        setTimelineError("")
        setFollowUp(null)
        setEditingNode(null)

        try {
            const response = await followUpService.getEnquiryFollowUps(enquiry.enquiryId)
            setFollowUp(response)
            setNodeForm({
                body: "",
                tag: FOLLOWUP_EVENT_TAGS.CLIENT_CALLED,
                followUpNextDate: getDefaultFollowUpDate(),
            })
        } catch (err) {
            console.error("Failed to load follow-up timeline:", err)
            setFollowUp(null)
            setTimelineError(err.message || "Failed to load follow-up timeline")
        } finally {
            setTimelineLoading(false)
        }
    }, [error])

    const resetNodeForm = useCallback(() => {
        setEditingNode(null)
        setNodeForm({
            body: "",
            tag: FOLLOWUP_EVENT_TAGS.CLIENT_CALLED,
            followUpNextDate: getDefaultFollowUpDate(),
        })
    }, [])

    const handleEditTimelineNode = useCallback((event) => {
        if (!followUp?.followUpNodes || !event?.id) return

        const node = followUp.followUpNodes.find((item) => item.followUpNodeId === event.id)
        if (!node) return

        setEditingNode(node)
        setNodeForm({
            body: node.body || "",
            tag: node.tag || FOLLOWUP_EVENT_TAGS.CLIENT_CALLED,
            followUpNextDate: getDefaultFollowUpDate(),
        })
    }, [followUp])

    const handleAddTimelineNote = useCallback(async () => {
        if (!followUp?.followUpId) {
            error("No follow-up thread found for this enquiry")
            return
        }

        if (!nodeForm.body.trim() || !nodeForm.followUpNextDate) {
            error("Please enter a follow-up note and next follow-up date")
            return
        }

        try {
            setAddingNote(true)
            if (editingNode) {
                await followUpService.updateFollowUpNode(followUp.followUpId, editingNode.followUpNodeId, nodeForm)
                success("Follow-up note updated successfully")
            } else {
                await followUpService.addFollowUpNode(followUp.followUpId, nodeForm)
                success("Follow-up note added successfully")
            }
            // Refresh follow-up data
            const refreshed = await followUpService.getEnquiryFollowUps(selectedEnquiry.enquiryId)
            setFollowUp(refreshed)
            resetNodeForm()
        } catch (err) {
            console.error("Failed to add follow-up note:", err)
            error(err.message || "Failed to save follow-up note")
        } finally {
            setAddingNote(false)
        }
    }, [editingNode, error, followUp, nodeForm, resetNodeForm, selectedEnquiry, success])

    const scrollToAddNote = () => {
        const element = document.getElementById("enquiry-add-note-section")
        if (element) {
            element.scrollIntoView({ behavior: "smooth" })
        }
    }

    const handleSubmit = async () => {
        if (!form.leadName || !validatePhone(form.leadMobileNumber) || !validateEmail(form.leadEmail)) {
            error("Please provide a valid lead name, mobile number, and email")
            return
        }

        if (!form.projectId || !form.propertyType || !form.property || !form.area || !form.budget || !form.reference || !form.referenceName) {
            error("Please complete the enquiry details before saving")
            return
        }

        if (editingEnquiry) {
            updateMutation.mutate(
                { id: editingEnquiry.enquiryId, payload: buildUpdatePayload(form) },
                {
                    onSuccess: () => {
                        success("Enquiry updated successfully")
                        setShowModal(false)
                        setEditingEnquiry(null)
                        setForm(buildInitialForm())
                        setPropertyOptions(null)
                    },
                    onError: (err) => {
                        error(err.message || "Failed to update enquiry")
                    }
                }
            )
        } else {
            createMutation.mutate(
                buildCreatePayload(form),
                {
                    onSuccess: () => {
                        success("Enquiry created successfully")
                        setShowModal(false)
                        setEditingEnquiry(null)
                        setForm(buildInitialForm())
                        setPropertyOptions(null)
                    },
                    onError: (err) => {
                        error(err.message || "Failed to create enquiry")
                    }
                }
            )
        }
    }

    const isSubmitting = createMutation.isPending || updateMutation.isPending

    const columns = [
        {
            key: "leadName",
            label: "Lead",
            render: (value, row) => (
                <div>
                    <p className="font-medium text-gray-900">{value}</p>
                    <p className="text-xs text-gray-500">{row.leadMobileNumber || "No mobile"}</p>
                </div>
            ),
        },
        {
            key: "projectName",
            label: "Project",
            render: (value) => <p className="text-sm text-gray-700">{value}</p>,
        },
        {
            key: "property",
            label: "Preference",
            render: (value, row) => (
                <p className="text-sm text-gray-700">{[row.propertyType, value, row.area ? `${row.area} sq ft` : null].filter(Boolean).join(" · ") || "Not specified"}</p>
            ),
        },
        {
            key: "budget",
            label: "Budget",
            render: (value) => <p className="text-sm font-medium text-gray-900">{formatCurrency(Number(value || 0))}</p>,
        },
        {
            key: "status",
            label: "Status",
            render: (value) => <Badge status={value}>{value}</Badge>,
        },
    ]

    if (loading) {
        return (
            <AppLayout>
                <SkeletonLoader type="table" count={5} />
            </AppLayout>
        )
    }

    return (
        <AppLayout>
            <div className="space-y-6">
                <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                    <div>
                        <h1 className="text-2xl md:text-3xl font-bold text-gray-900">Enquiry Book</h1>
                        <p className="mt-1 text-sm md:text-base text-gray-600">
                            Capture and manage leads and enquiries for your projects.
                        </p>
                    </div>

                    <Button onClick={openCreateModal}>
                        <Plus size={18} />
                        Add Enquiry
                    </Button>
                </div>

                <div className="flex flex-col gap-3 md:flex-row">
                    <div className="relative flex-1">
                        <Search size={18} className="absolute left-3 top-2.5 text-gray-400" />
                        <input
                            type="text"
                            placeholder="Search by lead, mobile, email, project, source, or budget..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full rounded-xl border border-gray-300 py-2 pl-10 pr-4 text-sm outline-none transition focus:border-transparent focus:ring-2 focus:ring-indigo-600"
                        />
                    </div>

                    <select
                        value={filters.projectId}
                        onChange={(e) => setFilters((prev) => ({ ...prev, projectId: e.target.value }))}
                        className="rounded-xl border border-gray-300 px-4 py-2 text-sm outline-none transition focus:border-transparent focus:ring-2 focus:ring-indigo-600"
                    >
                        <option value="">All projects</option>
                        {projects.map((project) => (
                            <option key={project.projectId} value={project.projectId}>
                                {project.projectName}
                            </option>
                        ))}
                    </select>

                    <select
                        value={filters.status}
                        onChange={(e) => setFilters((prev) => ({ ...prev, status: e.target.value }))}
                        className="rounded-xl border border-gray-300 px-4 py-2 text-sm outline-none transition focus:border-transparent focus:ring-2 focus:ring-indigo-600"
                    >
                        <option value="">All statuses</option>
                        <option value="ONGOING">Ongoing</option>
                        <option value="HOT_LEAD">Hot Lead</option>
                        <option value="WARM_LEAD">Warm Lead</option>
                        <option value="COLD_LEAD">Cold Lead</option>
                        <option value="BOOKED">Booked</option>
                        <option value="CANCELLED">Cancelled</option>
                    </select>
                </div>

                <Card>
                    <div className="overflow-x-auto">
                        <Table
                            columns={columns}
                            data={filteredEnquiries}
                            onRowClick={(row) => navigate(`/enquiry-book/${row.enquiryId}`)}
                            actions={(row) => [
                                {
                                    label: "Edit Enquiry",
                                    onClick: () => openEditModal(row),
                                },
                                {
                                    label: "View Timeline",
                                    onClick: () => handleViewTimeline(row),
                                },
                            ]}
                        />
                    </div>
                </Card>
            </div>

            <Modal
                isOpen={showModal}
                onClose={() => !isSubmitting && setShowModal(false)}
                title={editingEnquiry ? "Edit Enquiry" : "Add Enquiry"}
                size="5xl"
                variant="form"
                contentClassName="py-4 sm:py-5"
                footer={
                    <div className="flex justify-end gap-3">
                        <Button variant="outline" onClick={() => setShowModal(false)} disabled={isSubmitting}>
                            Cancel
                        </Button>
                        <Button onClick={handleSubmit} disabled={isSubmitting}>
                            {isSubmitting && <Loader2 size={16} className="animate-spin" />}
                            {editingEnquiry ? "Update Enquiry" : "Create Enquiry"}
                        </Button>
                    </div>
                }
            >
                <div className="grid gap-6 lg:grid-cols-2">
                    <div>
                        <h3 className="mb-3 text-base font-semibold text-gray-900">Lead Information</h3>
                        <div className="grid gap-x-3 sm:grid-cols-2">
                            <FormInput
                                label="Lead Name"
                                value={form.leadName}
                                onChange={(e) => setForm((prev) => ({ ...prev, leadName: e.target.value }))}
                                required
                            />
                            <FormInput
                                label="Mobile Number"
                                value={form.leadMobileNumber}
                                onChange={(e) => setForm((prev) => ({ ...prev, leadMobileNumber: e.target.value.replace(/\D/g, "").slice(0, 10) }))}
                                required
                            />
                            <FormInput
                                label="Landline Number"
                                value={form.leadLandlineNumber}
                                onChange={(e) => setForm((prev) => ({ ...prev, leadLandlineNumber: e.target.value.replace(/\D/g, "").slice(0, 10) }))}
                            />
                            <FormInput
                                label="Email"
                                type="email"
                                value={form.leadEmail}
                                onChange={(e) => setForm((prev) => ({ ...prev, leadEmail: e.target.value }))}
                                required
                            />
                            <FormInput
                                label="City"
                                value={form.leadCity}
                                onChange={(e) => setForm((prev) => ({ ...prev, leadCity: e.target.value }))}
                                required
                            />
                            <FormInput
                                label="Occupation"
                                value={form.leadOccupation}
                                onChange={(e) => setForm((prev) => ({ ...prev, leadOccupation: e.target.value }))}
                                required
                            />
                            <FormInput
                                label="Company"
                                value={form.leadCompany}
                                onChange={(e) => setForm((prev) => ({ ...prev, leadCompany: e.target.value }))}
                                required
                            />
                        </div>
                        <FormTextarea
                            label="Address"
                            value={form.leadAddress}
                            onChange={(e) => setForm((prev) => ({ ...prev, leadAddress: e.target.value }))}
                            rows={3}
                            required
                        />
                    </div>

                    <div>
                        <h3 className="mb-3 text-base font-semibold text-gray-900">Enquiry Information</h3>
                        <div className="grid gap-x-3 sm:grid-cols-2">
                            <FormSelect
                                label="Project"
                                value={form.projectId}
                                onChange={(e) => handleProjectChange(e.target.value)}
                                options={projects.map((project) => ({
                                    value: project.projectId,
                                    label: project.projectName,
                                }))}
                                placeholder="Select project"
                                disabled={Boolean(editingEnquiry)}
                                required
                            />
                            <FormSelect
                                label="Property Type"
                                value={form.propertyType}
                                onChange={(e) =>
                                    setForm((prev) => ({
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
                                value={form.property}
                                onChange={(e) =>
                                    setForm((prev) => ({
                                        ...prev,
                                        property: e.target.value,
                                        area: "",
                                    }))
                                }
                                options={properties}
                                placeholder="Select property"
                                disabled={!form.propertyType}
                            />
                            <FormSelect
                                label="Area"
                                value={form.area}
                                onChange={(e) => setForm((prev) => ({ ...prev, area: e.target.value }))}
                                options={areas}
                                placeholder="Select area"
                                disabled={!form.property}
                            />
                            <FormInput
                                label="Budget"
                                value={form.budget}
                                onChange={(e) => setForm((prev) => ({ ...prev, budget: e.target.value }))}
                                required
                            />
                            <FormInput
                                label="Reference"
                                value={form.reference}
                                onChange={(e) => setForm((prev) => ({ ...prev, reference: e.target.value }))}
                                required
                            />
                            <FormInput
                                label="Reference Name"
                                value={form.referenceName}
                                onChange={(e) => setForm((prev) => ({ ...prev, referenceName: e.target.value }))}
                                required
                            />
                        </div>
                        {editingEnquiry && (
                            <FormSelect
                                label="Status"
                                value={form.status}
                                onChange={(e) => setForm((prev) => ({ ...prev, status: e.target.value }))}
                                options={[
                                    { value: "ONGOING", label: "Ongoing" },
                                    { value: "HOT_LEAD", label: "Hot Lead" },
                                    { value: "WARM_LEAD", label: "Warm Lead" },
                                    { value: "COLD_LEAD", label: "Cold Lead" },
                                    { value: "BOOKED", label: "Booked" },
                                    { value: "CANCELLED", label: "Cancelled" },
                                ]}
                            />
                        )}
                        <FormTextarea
                            label="Remark"
                            value={form.remark}
                            onChange={(e) => setForm((prev) => ({ ...prev, remark: e.target.value }))}
                            rows={3}
                        />
                    </div>
                </div>
            </Modal>

            {/* Timeline Modal - Same as FollowUp Page */}
            <TwoColumnModal
                isOpen={showTimelineModal}
                onClose={() => {
                    if (addingNote) return
                    setShowTimelineModal(false)
                    setEditingNode(null)
                    setNodeForm({
                        body: "",
                        tag: FOLLOWUP_EVENT_TAGS.CLIENT_CALLED,
                        followUpNextDate: getDefaultFollowUpDate(),
                    })
                }}
                title="Follow-Up Timeline"
                leftContent={
                    selectedEnquiry && (
                        <div className="flex flex-col h-full">

                            <div className="mb-6 bg-gray-50 p-4 rounded-xl border border-gray-100 flex items-center justify-between">
                                <p className="text-md font-bold text-gray-900">{selectedEnquiry.leadName}</p>
                                <div className="flex items-center gap-2">
                                    <Phone size={14} className="text-gray-400" />
                                    <span className="text-sm font-medium text-gray-700">{selectedEnquiry.leadMobileNumber}</span>
                                </div>
                            </div>

                            <div className="flex-1 overflow-y-auto pr-2 max-h-[calc(90vh-350px)]">
                                <div className="flex items-center justify-between mb-4 sticky top-0 bg-white z-20 py-2">
                                    <div className="flex items-center gap-2">
                                        <Calendar size={20} className="text-purple-600" />
                                        <h3 className="font-semibold text-gray-900">Activity Timeline</h3>
                                    </div>
                                    <button
                                        onClick={scrollToAddNote}
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
                    )
                }
                rightContent={
                    selectedEnquiry && (
                        <div className="space-y-6 px-2 overflow-y-auto max-h-[calc(90vh-200px)]">
                            <ModalSection title="Follow-Up Details" icon={Calendar} iconColor="text-indigo-600">
                                <div className="space-y-4">
                                    <div>
                                        <p className="text-sm md:text-md text-gray-600 mb-1">Next Follow-Up Date</p>
                                        <p className="text-sm md:text-base font-semibold text-gray-900">
                                            {followUp?.followUpNextDate ? formatDate(followUp.followUpNextDate) : "Not scheduled"}
                                        </p>
                                    </div>
                                </div>
                            </ModalSection>

                            <div id="enquiry-add-note-section">
                                <ModalSection
                                    title={editingNode ? "Edit Note" : "Add New Note"}
                                    icon={FileText}
                                    iconColor="text-green-600"
                                >
                                    <div className="space-y-3">
                                        {selectedEnquiry.status === "BOOKED" || selectedEnquiry.status === "CANCELLED" ? (
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
                                            value={nodeForm.body}
                                            onChange={(e) => setNodeForm({ ...nodeForm, body: e.target.value })}
                                            placeholder="Enter your note..."
                                            disabled={noteEditingBlocked}
                                        />
                                        <FormSelect
                                            label="Event Tag"
                                            value={nodeForm.tag}
                                            onChange={(e) => setNodeForm({ ...nodeForm, tag: e.target.value })}
                                            options={Object.entries(FOLLOWUP_EVENT_TAGS).map(([key, value]) => ({
                                                value: value,
                                                label: value,
                                            }))}
                                            disabled={noteEditingBlocked}
                                            required
                                        />
                                        <FormInput
                                            type="date"
                                            label="Next Follow Up Date"
                                            value={nodeForm.followUpNextDate}
                                            onChange={(e) => setNodeForm({ ...nodeForm, followUpNextDate: e.target.value })}
                                            disabled={noteEditingBlocked}
                                        />

                                        <Button onClick={handleAddTimelineNote} variant="primary" className="w-full" loading={addingNote} disabled={noteEditingBlocked}>
                                            {editingNode ? "Save Changes" : "Add Note"}
                                        </Button>
                                        {editingNode && (
                                            <Button
                                                variant="secondary"
                                                className="w-full"
                                                onClick={() => {
                                                    setEditingNode(null)
                                                    setNodeForm({
                                                        body: "",
                                                        tag: FOLLOWUP_EVENT_TAGS.CLIENT_CALLED,
                                                        followUpNextDate: getDefaultFollowUpDate(),
                                                    })
                                                }}
                                                disabled={addingNote}
                                            >
                                                Cancel Edit
                                            </Button>
                                        )}
                                    </div>
                                </ModalSection>
                            </div>
                        </div>
                    )
                }
                columnGap="lg"
            />
        </AppLayout>
    )
}
