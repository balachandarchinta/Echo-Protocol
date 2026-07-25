import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

const rawPort = process.env.ZC_SLATE_PORT || process.env.PORT
const parsedPort =
  rawPort && !isNaN(parseInt(rawPort, 10))
    ? parseInt(rawPort, 10)
    : 5174

export default defineConfig({
  base: "./",
  plugins: [react()],
  server: {
    port: parsedPort,
    host: true,
  },
})
