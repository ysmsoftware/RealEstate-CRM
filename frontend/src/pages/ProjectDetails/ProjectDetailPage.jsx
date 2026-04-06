import { useState, useEffect, useCallback } from "react"
import { useParams, useNavigate } from "react-router-dom"
import { useQueryClient } from "@tanstack/react-query"
import { useToast } from "../../components/ui/Toast" // Adjusted path
import { useProject, useProjectEnquiries, useDisbursements, useAmenities, useBanks, useDocuments } from "../../api/hooks/useProjects"
import { KEYS } from "../../api/keys"
import { useAuth } from "../../contexts/AuthContext"
import { AppLayout } from "../../components/layout/AppLayout"
import { Tabs } from "../../components/ui/Tabs"
import { Button } from "../../components/ui/Button"
import { SkeletonLoader } from "../../components/ui/SkeletonLoader"
import { ArrowLeft, Trash2 } from "lucide-react"
import { projectService } from "../../services/projectService"

// Import Tab Components
import OverviewTab from "./tabs/OverviewTab"
import WingsTab from "./tabs/WingsTab"
import BanksTab from "./tabs/BanksTab"
import AmenitiesTab from "./tabs/AmenitiesTab"
import DocumentsTab from "./tabs/DocumentsTab"
import PaymentStagesTab from "./tabs/PaymentStagesTab"
import EnquiriesTab from "./tabs/EnquiriesTab"

export default function ProjectDetailPage() {
    const { projectId } = useParams()
    const navigate = useNavigate()
    const { error: toastError } = useToast()
    const { user } = useAuth()

    const queryClient = useQueryClient()

    const { data: project, isLoading: loading } = useProject(projectId)
    const { data: enquiries = [] } = useProjectEnquiries(projectId)
    const { data: disbursements = [] } = useDisbursements(projectId)
    const { data: amenities = [] } = useAmenities(projectId)
    const { data: banks = [] } = useBanks(projectId)
    const { data: documents = [] } = useDocuments(projectId)

    const refreshBanks = () => queryClient.invalidateQueries({ queryKey: KEYS.banks(projectId) })
    const refreshAmenities = () => queryClient.invalidateQueries({ queryKey: KEYS.amenities(projectId) })
    const refreshDocuments = () => queryClient.invalidateQueries({ queryKey: KEYS.documents(projectId) })
    const fetchData = () => {
        queryClient.invalidateQueries({ queryKey: KEYS.project(projectId) })
        queryClient.invalidateQueries({ queryKey: KEYS.projectEnquiries(projectId) })
        queryClient.invalidateQueries({ queryKey: KEYS.disbursements(projectId) })
    }

    // --- Main Render Helpers ---
    if (loading) {
        return (
            <AppLayout>
                <SkeletonLoader type={"stat"} count={4} />
                <SkeletonLoader type={"table"} count={5} />
            </AppLayout>
        )
    }

    if (!project) {
        return (
            <AppLayout>
                <div className="p-4 text-center">Project not found</div>
            </AppLayout>
        )
    }

    const tabs = [
        {
            label: "Overview",
            content: <OverviewTab project={project} projectId={projectId} onUpdate={fetchData} />,
        },
        {
            label: "Wings",
            content: <WingsTab project={project} projectId={projectId} onRefresh={fetchData} />,
        },
        {
            label: "Bank Info",
            content: <BanksTab banks={banks} projectId={projectId} onRefresh={refreshBanks} />,
        },
        {
            label: "Amenities",
            content: (
                <AmenitiesTab amenities={amenities} projectId={projectId} onRefresh={refreshAmenities} />
            ),
        },
        {
            label: "Documents",
            content: (
                <DocumentsTab documents={documents} projectId={projectId} onRefresh={refreshDocuments} />
            ),
        },
        {
            label: "Payment Stages",
            content: (
                <PaymentStagesTab
                    disbursements={disbursements}
                    projectId={projectId}
                    onRefresh={fetchData}
                />
            ),
        },
        {
            label: "Enquiries",
            content: <EnquiriesTab enquiries={enquiries} />,
        },
    ]

    return (
        <AppLayout>
            <div className="space-y-6 pb-12">
                {/* Header */}
                <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                    <div className="flex items-center gap-4 w-full sm:w-auto">
                        <button
                            onClick={() => navigate("/projects")}
                            className="p-2 hover:bg-gray-100 rounded-lg flex-shrink-0"
                        >
                            <ArrowLeft size={24} className="text-gray-600" />
                        </button>
                        <div className="min-w-0">
                            <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 break-words">
                                {project.projectName}
                            </h1>
                            <p className="text-gray-600 mt-1 text-sm break-words flex items-center gap-2">
                                <span className="inline-block w-2 h-2 rounded-full bg-green-500"></span>
                                {project.projectAddress}
                            </p>
                        </div>
                    </div>
                    <div className="flex gap-2 w-full sm:w-auto flex-col-reverse sm:flex-row">
                        {user?.role === "ADMIN" && (
                            <Button
                                variant="danger"
                                className="w-full sm:w-auto"
                                onClick={async () => {
                                    if (window.confirm("Are you sure? This cannot be undone.")) {
                                        await projectService.deleteProject(projectId)
                                        navigate("/projects")
                                    }
                                }}
                            >
                                <Trash2 size={20} className="mr-2" />
                                Delete Project
                            </Button>
                        )}
                    </div>
                </div>

                <Tabs tabs={tabs} />
            </div>
        </AppLayout>
    )
}