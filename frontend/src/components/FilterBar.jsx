import { useState, useEffect } from 'react'
import { salonApi } from '../services/api'
import './FilterBar.scss'

// Available filter fields with display names and input types
const AVAILABLE_FIELDS = [
  { value: 'name', label: 'Name', type: 'text', placeholder: 'Salon name' },
  { value: 'address', label: 'Address', type: 'text', placeholder: 'Street address' },
  { value: 'district', label: 'District', type: 'text', placeholder: 'e.g., Śródmieście' },
  { value: 'phone', label: 'Phone', type: 'text', placeholder: 'Phone number' },
  { value: 'services', label: 'Service', type: 'text', placeholder: 'Haircut, Coloring...' },
  { value: 'lowestPrice', label: 'Min Price (zł)', type: 'number', placeholder: '0' },
  { value: 'highestPrice', label: 'Max Price (zł)', type: 'number', placeholder: '1000' },
  { value: 'rating', label: 'Min Rating', type: 'number', step: 0.1, min: 0, max: 5, placeholder: '4.0' },
  { value: 'numberOfReviews', label: 'Min Reviews', type: 'number', placeholder: '100' },
]

function FilterBar({ onFiltersChange }) {
  const [activeFilters, setActiveFilters] = useState({})
  const [selectedField, setSelectedField] = useState(AVAILABLE_FIELDS[0].value)
  const [inputValue, setInputValue] = useState('')
  const [showAddRow, setShowAddRow] = useState(false)
  const [districts, setDistricts] = useState([])

  // Load districts for convenience (still useful)
  useEffect(() => {
    const fetchDistricts = async () => {
      try {
        const response = await salonApi.getAllSalons(0, 100)
        const uniqueDistricts = [
          ...new Set(response.data.map((salon) => salon.district).filter(Boolean)),
        ].sort()
        setDistricts(uniqueDistricts)
      } catch (err) {
        console.error('Failed to fetch districts', err)
      }
    }
    fetchDistricts()
  }, [])

  // Notify parent when filters change
  useEffect(() => {
    onFiltersChange(activeFilters)
  }, [activeFilters, onFiltersChange])

  const addFilter = () => {
    if (!inputValue.trim()) return

    const fieldInfo = AVAILABLE_FIELDS.find(f => f.value === selectedField)
    let value = inputValue.trim()

    // Convert numeric fields
    if (fieldInfo.type === 'number') {
      const num = parseFloat(value)
      if (isNaN(num)) return
      value = num
    }

    setActiveFilters(prev => ({
      ...prev,
      [selectedField]: value
    }))
    setInputValue('')
    setShowAddRow(false)
    setSelectedField(AVAILABLE_FIELDS[0].value)
  }

  const removeFilter = (field) => {
    setActiveFilters(prev => {
      const newFilters = { ...prev }
      delete newFilters[field]
      return newFilters
    })
  }

  const clearAllFilters = () => {
    setActiveFilters({})
    setShowAddRow(false)
    setInputValue('')
  }

  const getFieldLabel = (field) => {
    return AVAILABLE_FIELDS.find(f => f.value === field)?.label || field
  }

  const formatValue = (field, value) => {
    if (field === 'lowestPrice' || field === 'highestPrice') return `${value} zł`
    if (field === 'rating') return `⭐ ${value}`
    if (field === 'numberOfReviews') return `${value} reviews`
    return value
  }

  const selectedFieldConfig = AVAILABLE_FIELDS.find(f => f.value === selectedField)

  return (
    <div className="filter-bar">
      <div className="filter-header">
        <h3>Filters</h3>
        {Object.keys(activeFilters).length > 0 && (
          <button onClick={clearAllFilters} className="clear-all-btn">
            Clear all
          </button>
        )}
      </div>

      {/* Active filter chips */}
      {Object.keys(activeFilters).length > 0 && (
        <div className="active-filters">
          {Object.entries(activeFilters).map(([field, value]) => (
            <div key={field} className="filter-chip">
              <span className="filter-label">{getFieldLabel(field)}:</span>
              <span className="filter-value">{formatValue(field, value)}</span>
              <button onClick={() => removeFilter(field)} className="remove-filter" aria-label="Remove">
                ×
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Quick district filter (convenience) */}
      <div className="filter-row">
        <div className="filter-group">
          <label>District (quick)</label>
          <select
            value={activeFilters.district || ''}
            onChange={(e) => {
              const value = e.target.value
              if (value) {
                setActiveFilters(prev => ({ ...prev, district: value }))
              } else {
                removeFilter('district')
              }
            }}
          >
            <option value="">All Districts</option>
            {districts.map((district) => (
              <option key={district} value={district}>
                {district}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Add custom filter section */}
      {!showAddRow ? (
        <button onClick={() => setShowAddRow(true)} className="add-filter-btn">
          + Add custom filter
        </button>
      ) : (
        <div className="add-filter-row">
          <select
            value={selectedField}
            onChange={(e) => setSelectedField(e.target.value)}
            className="filter-field-select"
          >
            {AVAILABLE_FIELDS.map((field) => (
              <option key={field.value} value={field.value}>
                {field.label}
              </option>
            ))}
          </select>
          <input
            type={selectedFieldConfig?.type || 'text'}
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            placeholder={selectedFieldConfig?.placeholder || 'Enter value'}
            step={selectedFieldConfig?.step}
            min={selectedFieldConfig?.min}
            max={selectedFieldConfig?.max}
            className="filter-value-input"
            onKeyPress={(e) => e.key === 'Enter' && addFilter()}
          />
          <button onClick={addFilter} className="confirm-add-btn">Add</button>
          <button onClick={() => setShowAddRow(false)} className="cancel-add-btn">Cancel</button>
        </div>
      )}
    </div>
  )
}

export default FilterBar