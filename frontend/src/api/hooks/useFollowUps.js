import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { KEYS } from "../keys"
import { followUpService } from "../../services/followUpService"

export const useFollowUpTasks = () => {
    return useQuery({
        queryKey: KEYS.followUpTasks(),
        queryFn: () => followUpService.getFollowUpTasks(),
        staleTime: 30_000,
    })
}

export const useFollowUpById = (id) => {
    return useQuery({
        queryKey: KEYS.followUp(id),
        queryFn: () => followUpService.getFollowUpById(id),
        enabled: !!id,
    })
}

export const useEnquiryFollowUp = (enquiryId) => {
    return useQuery({
        queryKey: KEYS.enquiryFollowUp(enquiryId),
        queryFn: () => followUpService.getEnquiryFollowUps(enquiryId),
        enabled: !!enquiryId,
    })
}

export const useAddFollowUpNode = () => {
    return useMutation({
        mutationFn: ({ followUpId, data }) => followUpService.addFollowUpNode(followUpId, data),
    })
}

export const useUpdateFollowUpNode = () => {
    return useMutation({
        mutationFn: ({ followUpId, nodeId, data }) => followUpService.updateFollowUpNode(followUpId, nodeId, data),
    })
}
