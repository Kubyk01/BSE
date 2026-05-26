import axios from 'axios'

const API_BASE_URL = import.meta.env.API_URL || 'http://localhost:8080'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const salonApi = {
  // Get all salons with pagination
  getAllSalons: (page = 0, size = 12, sort = null) => {
    let url = `/salons?page=${page}&size=${size}`
    if (sort) url += `&sort=${sort}`
    return apiClient.get(url)
  },

  // Get salon by ID
  getSalonById: (id) => apiClient.get(`/salons/${id}`),

  // Update salon
  updateSalon: (id, salonData) => apiClient.put(`/salons/${id}`, salonData),

  // Delete salon
  deleteSalon: (id) => apiClient.delete(`/salons/${id}`),

  // Filter salons by fields (e.g., district)
  filterSalons: (filters, page = 0, size = 12, sort = null) => {
    const params = new URLSearchParams({
      page,
      size,
      ...(sort && { sort }),
      ...filters,
    })
    return apiClient.get(`/salons/filter?${params.toString()}`)
  },
}

export default salonApi