import { useQuery } from "@tanstack/react-query"
import { KEYS } from "../keys"
import { projectService } from "../../services/projectService"

export const useProjects = () => {
    return useQuery({
        queryKey: KEYS.projects(),
        queryFn: () => projectService.getProjects(),
        staleTime: 60_000,
    })
}

export const useProject = (projectId) => {
    return useQuery({
        queryKey: KEYS.project(projectId),
        queryFn: () => projectService.getProjectById(projectId),
        staleTime: 60_000,
        enabled: !!projectId,
    })
}

export const useProjectEnquiries = (projectId) => {
    return useQuery({
        queryKey: KEYS.projectEnquiries(projectId),
        queryFn: () => projectService.getProjectEnquiries(projectId),
        staleTime: 60_000,
        enabled: !!projectId,
    })
}

export const useDisbursements = (projectId) => {
    return useQuery({
        queryKey: KEYS.disbursements(projectId),
        queryFn: () => projectService.getDisbursements(projectId),
        staleTime: 60_000,
        enabled: !!projectId,
    })
}

export const useAmenities = (projectId) => {
    return useQuery({
        queryKey: KEYS.amenities(projectId),
        queryFn: () => projectService.getAmenitiesByProject(projectId),
        staleTime: 60_000,
        enabled: !!projectId,
    })
}

export const useBanks = (projectId) => {
    return useQuery({
        queryKey: KEYS.banks(projectId),
        queryFn: () => projectService.getBanksByProject(projectId),
        staleTime: 60_000,
        enabled: !!projectId,
    })
}

export const useDocuments = (projectId) => {
    return useQuery({
        queryKey: KEYS.documents(projectId),
        queryFn: () => projectService.getDocumentsByProject(projectId),
        staleTime: 60_000,
        enabled: !!projectId,
    })
}
