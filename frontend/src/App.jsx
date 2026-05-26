import { Routes, Route } from 'react-router-dom'
import ListingPage from './pages/ListingPage'
import DetailPage from './pages/DetailPage'

function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>💇‍♀️ Beauty Salon Explorer</h1>
        <p>Discover the best salons in Warsaw</p>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<ListingPage />} />
          <Route path="/salon/:id" element={<DetailPage />} />
        </Routes>
      </main>
    </div>
  )
}

export default App