import { useState, useEffect } from 'react'
import './EditSalonModal.scss'

function EditSalonModal({ isOpen, onClose, salon, onSave }) {
  const [formData, setFormData] = useState({
    name: '',
    address: '',
    district: '',
    phone: '',
    website: {},
    services: [],
    lowestPrice: 0,
    highestPrice: 0,
    rating: 0,
    numberOfReviews: 0,
  })
  const [servicesInput, setServicesInput] = useState('')

  useEffect(() => {
    if (salon) {
      setFormData({
        name: salon.name || '',
        address: salon.address || '',
        district: salon.district || '',
        phone: salon.phone || '',
        website: salon.website || {},
        services: salon.services || [],
        lowestPrice: salon.lowestPrice || 0,
        highestPrice: salon.highestPrice || 0,
        rating: salon.rating || 0,
        numberOfReviews: salon.numberOfReviews || 0,
      })
      setServicesInput((salon.services || []).join(', '))
    }
  }, [salon])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleServicesChange = (e) => {
    const value = e.target.value
    setServicesInput(value)
    const servicesArray = value.split(',').map((s) => s.trim()).filter(Boolean)
    setFormData((prev) => ({ ...prev, services: servicesArray }))
  }

  const handleWebsiteChange = (e) => {
    const value = e.target.value
    setFormData((prev) => ({
      ...prev,
      website: { website: value },
    }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    const submitData = {
      ...formData,
      lowestPrice: parseFloat(formData.lowestPrice),
      highestPrice: parseFloat(formData.highestPrice),
      rating: parseFloat(formData.rating),
      numberOfReviews: parseInt(formData.numberOfReviews, 10),
    }
    onSave(submitData)
  }

  if (!isOpen) return null

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h2>Edit Salon</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Name *</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Address *</label>
            <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>District *</label>
            <input
              type="text"
              name="district"
              value={formData.district}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Phone</label>
            <input
              type="text"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Website URL</label>
            <input
              type="text"
              name="website"
              value={formData.website?.website || ''}
              onChange={handleWebsiteChange}
              placeholder="example.com"
            />
          </div>

          <div className="form-group">
            <label>Services (comma separated)</label>
            <input
              type="text"
              value={servicesInput}
              onChange={handleServicesChange}
              placeholder="Haircut, Coloring, Styling"
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Lowest Price (zł)</label>
              <input
                type="number"
                name="lowestPrice"
                value={formData.lowestPrice}
                onChange={handleChange}
                step="0.01"
              />
            </div>
            <div className="form-group">
              <label>Highest Price (zł)</label>
              <input
                type="number"
                name="highestPrice"
                value={formData.highestPrice}
                onChange={handleChange}
                step="0.01"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Rating</label>
              <input
                type="number"
                name="rating"
                value={formData.rating}
                onChange={handleChange}
                step="0.1"
                min="0"
                max="5"
              />
            </div>
            <div className="form-group">
              <label>Number of Reviews</label>
              <input
                type="number"
                name="numberOfReviews"
                value={formData.numberOfReviews}
                onChange={handleChange}
                step="1"
                min="0"
              />
            </div>
          </div>

          <div className="modal-actions">
            <button type="button" onClick={onClose} className="cancel-btn">
              Cancel
            </button>
            <button type="submit" className="save-btn">
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default EditSalonModal