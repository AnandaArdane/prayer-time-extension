# Stage 1: Build the application
FROM node:20-slim AS build
RUN apt-get update -y && apt-get install -y openssl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY backend-js/package*.json ./
COPY backend-js/prisma ./prisma/
RUN npm ci
COPY backend-js/ ./
RUN npx prisma generate
RUN npm run build

# Stage 2: Run the application
# Copy full node_modules from build stage to avoid re-downloading Prisma engines
FROM node:20-slim
RUN apt-get update -y && apt-get install -y openssl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/dist ./dist
COPY backend-js/package*.json ./
EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["node", "dist/server.js"]
