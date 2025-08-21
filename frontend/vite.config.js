import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 8084,
    host: '0.0.0.0',
    allowedHosts: [
      'localhost',
      '127.0.0.1',
      'soldiers.share.zrok.io',
      'soldiersservice.share.zrok.io',
      '.zrok.io'
    ]
  }
}) 