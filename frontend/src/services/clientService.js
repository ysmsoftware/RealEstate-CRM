import { apiClient } from "./apiClient"

// Post-booking clients only.
export const clientService = {
    async getClientsBasicInfo() {
        try {
            const response = await apiClient.request("/clients/basicinfolist")
            console.log("Booked clients basic info fetched")
            return response
        } catch (error) {
            console.error("Failed to fetch booked clients basic info:", error)
            throw error
        }
    },

    async getClientById(clientId) {
        try {
            const response = await apiClient.request(`/clients/${clientId}/details`)
            console.log("Booked client fetched:", clientId)
            return response
        } catch (error) {
            console.error("Failed to fetch booked client:", error)
            throw error
        }
    },

    async updateClient(clientId, clientData) {
        try {
            const response = await apiClient.request(`/clients/${clientId}`, {
                method: "PUT",
                body: JSON.stringify(clientData),
            })

            console.log("Booked client updated:", clientId)
            return response
        } catch (error) {
            console.error("Failed to update booked client:", error)
            throw error
        }
    },
}
