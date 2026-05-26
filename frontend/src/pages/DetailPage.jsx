import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { salonApi } from '../services/api'
import EditSalonModal from '../components/EditSalonModal'
import './DetailPage.scss'

function DetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [salon, setSalon] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [isEditModalOpen, setIsEditModalOpen] = useState(false)
  const [deleteConfirm, setDeleteConfirm] = useState(false)

  useEffect(() => {
    fetchSalonDetail()
  }, [id])

  const fetchSalonDetail = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await salonApi.getSalonById(id)
      setSalon(response.data)
    } catch (err) {
      setError('Failed to load salon details.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleUpdateSalon = async (updatedData) => {
    try {
      const response = await salonApi.updateSalon(id, updatedData)
      setSalon(response.data)
      setIsEditModalOpen(false)
    } catch (err) {
      alert('Failed to update salon. Please try again.')
      console.error(err)
    }
  }

  const handleDeleteSalon = async () => {
    if (!deleteConfirm) {
      setDeleteConfirm(true)
      setTimeout(() => setDeleteConfirm(false), 3000)
      return
    }
    try {
      await salonApi.deleteSalon(id)
      navigate('/', { replace: true })
    } catch (err) {
      alert('Failed to delete salon.')
      console.error(err)
    }
  }

  if (loading) return <div className="loading-spinner">Loading salon details...</div>
  if (error) return <div className="error-message">{error}</div>
  if (!salon) return <div className="error-message">Salon not found</div>

  return (
    <div className="detail-page">
      <button onClick={() => navigate('/')} className="back-btn">
        ← Back to listings
      </button>

      <div className="detail-card">
        <div className="detail-header">
          <h1>{salon.name}</h1>
          <div className="detail-actions">
            <button onClick={() => setIsEditModalOpen(true)} className="edit-btn">
              ✏️ Edit
            </button>
            <button
              onClick={handleDeleteSalon}
              className={`delete-btn ${deleteConfirm ? 'confirm' : ''}`}
            >
              {deleteConfirm ? 'Confirm Delete' : '🗑️ Delete'}
            </button>
          </div>
        </div>

        <div className="detail-section">
          <h3>Location</h3>
          <p>
            <strong>Address:</strong> {salon.address}
          </p>
          <p>
            <strong>District:</strong> {salon.district}
          </p>
        </div>

        <div className="detail-section">
          <h3>Contact</h3>
          <p>
            <strong>Phone:</strong> {salon.phone || 'Not provided'}
          </p>
          {salon.website && (
            <p>
              <strong>Website:</strong>{' '}
              <a
                href={
                  salon.website.website
                    ? `https://${salon.website.website}`
                    : '#'
                }
                target="_blank"
                rel="noopener noreferrer"
              >
                {salon.website.website || 'Visit website'}
              </a>
            </p>
          )}
        </div>

        <div className="detail-section">
          <h3>Services</h3>
          <div className="services-list">
            {salon.services?.map((service, idx) => (
              <span key={idx} className="service-tag">
                {service}
              </span>
            ))}
            {(!salon.services || salon.services.length === 0) && (
              <p>No services listed</p>
            )}
          </div>
        </div>

        <div className="detail-section">
          <h3>Pricing</h3>
          <p>
            <strong>Price Range:</strong> {salon.lowestPrice} zł -{' '}
            {salon.highestPrice} zł
          </p>
        </div>

        <div className="detail-section">
          <h3>Reviews</h3>
          <p>
            <strong>Rating:</strong> {salon.rating || 'N/A'} ⭐
          </p>
          <p>
            <strong>Number of Reviews:</strong> {salon.numberOfReviews || 0}
          </p>
        </div>
      </div>

      <EditSalonModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        salon={salon}
        onSave={handleUpdateSalon}
      />
    </div>
  )
}

export default DetailPage