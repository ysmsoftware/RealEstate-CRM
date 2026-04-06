import { useQuery } from "@tanstack/react-query"
import { KEYS } from "../keys"
import { dashboardService } from "../../services/dashboardService"

export const useDashboard = () => {
    return useQuery({
        queryKey: KEYS.dashboard(),
        queryFn: dashboardService.getDashboard,
        staleTime: 60_000,
    })
}
