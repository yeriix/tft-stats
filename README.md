## How to run
1. Get an API key at developer.riotgames.com
2. Backend (PowerShell):
   `$env:RIOT_API_KEY="your-key"; cd backend; .\mvnw.cmd spring-boot:run`
   (Linux/macOS: `export RIOT_API_KEY=... && cd backend && ./mvnw spring-boot:run`)
3. Frontend: `cd frontend && npm install && ng serve`
4. Open http://localhost:4200
