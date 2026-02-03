import { apiClient } from "./apiClient"

export const authService = {
    async login(username, password) {
        try {
            const data = await apiClient.request("/login", {
                method: "POST",
                body: JSON.stringify({ username, password }),
            });

            // Store tokens
            apiClient.setTokens(data.accessToken, data.refreshToken)

            return {
                success: true,
                accessToken: data.accessToken,
                refreshToken: data.refreshToken,
                expiresInSeconds: data.expiresInSeconds,
                role: data.role,
            }
        } catch (error) {
            console.error("[v0] Login error:", error)
            throw error
        }
    },

    async registerOrganization(data) {
        try {
            const responseData = await apiClient.request("/register-organization", {
                method: "POST",
                body: JSON.stringify(data),
            })

            return responseData
        } catch (error) {
            console.error("[v0] Registration error:", error)
            throw error
        }
    },

    async logout() {
        apiClient.clearTokens()
        return true
    },

    isTokenValid() {
        const token = apiClient.getAuthToken()
        return !!token
    },
}
