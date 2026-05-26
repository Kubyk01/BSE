import './SalonCard.scss'

function SalonCard({ salon, onClick }) {
  const renderStars = (rating) => {
    if (!rating) return 'No ratings'
    const fullStars = Math.floor(rating)
    const hasHalfStar = rating % 1 >= 0.5
    return '⭐'.repeat(fullStars) + (hasHalfStar ? '½' : '')
  }

  return (
    <div className="salon-card" onClick={onClick}>
      <h3 className="salon-name">{salon.name}</h3>
      <p className="salon-district">{salon.district}</p>
      <div className="salon-rating">
        <span className="stars">{renderStars(salon.rating)}</span>
        <span className="rating-value">{salon.rating?.toFixed(1)}</span>
      </div>
      <p className="salon-price">
        {salon.lowestPrice} zł - {salon.highestPrice} zł
      </p>
      <div className="salon-services">
        {salon.services?.slice(0, 3).map((service, idx) => (
          <span key={idx} className="service-pill">
            {service}
          </span>
        ))}
        {salon.services?.length > 3 && (
          <span className="service-pill">+{salon.services.length - 3}</span>
        )}
      </div>
    </div>
  )
}

export default SalonCard