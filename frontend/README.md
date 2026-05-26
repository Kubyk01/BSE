# Beauty Salon Explorer – Frontend

React-based web application for browsing, filtering, and managing beauty salons in Warsaw.  
Connects to a reactive Spring Boot backend (R2DBC + PostgreSQL).  
Provides real‑time filtering, pagination, salon details, and full CRUD operations.

---

## Prerequisites

- Node.js 18+ (or 20+)
- npm or yarn
- Backend service running (see backend README)

---

## Run Locally

### 1. Install dependencies

```bash
npm install
```

### 2. Start the development server

```bash
npm run dev
```

The application will be available at http://localhost:5173


### 3. Build for production
```bash
npm run build
```

### 4. Build for production
```bash
npm run preview
```

make sure backend is running ;]

---


## Technologies Used

* React 18 – component‑based UI library
* React Router v6 – client‑side routing for listing and detail pages
* Vite – fast development server and build tool
* SCSS (Sass) – modular styling with CSS variables and nesting
* Axios – HTTP client for communication with the backend API
* React Hooks – state management (useState, useEffect, useCallback)
* Custom UI components – FilterBar, SalonCard, EditSalonModal
* Responsive design – mobile‑first breakpoints (768px, 1024px)


---


## Todo:
1. Advanced filtering - add min/max range inputs for numeric fields
2. Infinite scroll – replace pagination buttons with smooth infinite scrolling.
3. When in backend BeanUtils implements, change update to send only changed fields