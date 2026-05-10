# ================================
# Stage 1 — Build Frontend
# ================================

FROM node:18-alpine AS build

# Set working directory
WORKDIR /app

# Copy package files first for caching
COPY package*.json ./

# Install dependencies
# npm ci is faster and more reliable for production builds
RUN npm ci

# Copy application source code
COPY . .

# Build React production bundle
RUN npm run build

# ================================
# Stage 2 — Production Server
# ================================

FROM nginx:stable-alpine

# Metadata
LABEL maintainer="Ayush Rao Chaudhary"
LABEL service="frontend"
LABEL project="fintech-payment-system"

# Remove default nginx static files
RUN rm -rf /usr/share/nginx/html/*

# Copy React build files
COPY --from=build /app/build /usr/share/nginx/html

# Expose frontend port
EXPOSE 80

# Start nginx server
CMD ["nginx", "-g", "daemon off;"]