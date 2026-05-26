import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import SalonCard from '../components/SalonCard'
import FilterBar from '../components/FilterBar'
import { salonApi } from '../services/api'
import './ListingPage.scss'

function ListingPage() {
  const navigate = useNavigate()
  const [salons, setSalons] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [pagination, setPagination] = useState({
    page: 0,
    size: 12,
  })
  const [filters, setFilters] = useState({})

  const fetchSalons = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      let response
      if (Object.keys(filters).length > 0) {
        const stringFilters = {}
        for (const [key, value] of Object.entries(filters)) {
          stringFilters[key] = String(value)
        }
        response = await salonApi.filterSalons(
          stringFilters,
          pagination.page,
          pagination.size
        )
      } else {
        response = await salonApi.getAllSalons(pagination.page, pagination.size)
      }

      setSalons(response.data)
    } catch (err) {
      setError('Failed to load salons. Please try again later.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }, [filters, pagination.page, pagination.size])

  useEffect(() => {
    fetchSalons()
  }, [fetchSalons])

  const handleFiltersChange = useCallback((newFilters) => {
    setFilters(newFilters)
    setPagination(prev => ({ ...prev, page: 0 }))
  }, [])

  const handlePageChange = (newPage) => {
    setPagination(prev => ({ ...prev, page: newPage }))
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const handleSalonClick = (id) => {
    navigate(`/salon/${id}`)
  }

  return (
    <div className="listing-page">
      <FilterBar onFiltersChange={handleFiltersChange} />

      {loading && <div className="loading-spinner">Loading salons...</div>}
      {error && <div className="error-message">{error}</div>}

      {!loading && !error && (
        <>
          <div className="salons-grid">
            {salons.map((salon) => (
              <SalonCard
                key={salon.id || salon.name + salon.address}
                salon={salon}
                onClick={() => handleSalonClick(salon.id)}
              />
            ))}
          </div>

          {salons.length === 0 && (
            <div className="no-results">
              No salons found matching your criteria.
            </div>
          )}

          {salons.length > 0 && (
            <div className="pagination">
              <button
                onClick={() => handlePageChange(pagination.page - 1)}
                disabled={pagination.page === 0}
                className="pagination-btn"
              >
                Previous
              </button>
              <span className="page-info">Page {pagination.page + 1}</span>
              <button
                onClick={() => handlePageChange(pagination.page + 1)}
                disabled={salons.length < pagination.size}
                className="pagination-btn"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default ListingPage