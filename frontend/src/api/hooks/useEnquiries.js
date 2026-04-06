import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { KEYS } from "../keys"
import { enquiryService } from "../../services/enquiryService"

export const useEnquiries = () => {
    return useQuery({
        queryKey: KEYS.enquiries(),
        queryFn: () => enquiryService.getAllEnquiries(),
        staleTime: 30_000,
    })
}

export const useEnquiry = (id) => {
    return useQuery({
        queryKey: KEYS.enquiry(id),
        queryFn: () => enquiryService.getEnquiry(id),
        enabled: !!id,
    })
}

export const useCreateEnquiry = () => {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: (payload) => enquiryService.createEnquiry(payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: KEYS.enquiries() })
        },
    })
}

export const useUpdateEnquiry = () => {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: ({ id, payload }) => enquiryService.updateEnquiry(id, payload),
        onSuccess: (_, { id }) => {
            queryClient.invalidateQueries({ queryKey: KEYS.enquiries() })
            queryClient.invalidateQueries({ queryKey: KEYS.enquiry(id) })
        },
    })
}

export const useCancelEnquiry = () => {
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: ({ id, remark }) => enquiryService.cancelEnquiry(id, remark),
        onSuccess: (_, { id }) => {
            queryClient.invalidateQueries({ queryKey: KEYS.enquiries() })
            queryClient.invalidateQueries({ queryKey: KEYS.enquiry(id) })
        },
    })
}
