# MTOM to JSON Parser - Frontend

Angular 17 web applicatie voor het uploaden en converteren van MTOM berichten naar JSON.

## Features

- **File Upload**: Upload MTOM XML bestanden via file picker
- **Client Configuration**: Selecteer de juiste klantconfiguratie voor parsing
- **Real-time Conversion**: Converteer MTOM naar JSON met één klik
- **Validation**: Valideer MTOM berichten zonder volledige conversie
- **JSON Viewer**: Gestructureerde weergave van geparseerde data met:
  - Message informatie (ID, client, timestamp, processing time)
  - Metadata fields in overzichtelijke grid
  - Content informatie (filename, MIME type, size)
  - Raw JSON view met syntax highlighting
- **Error Handling**: Duidelijke error messages met details
- **Download**: Download geparseerde JSON als bestand
- **Responsive Design**: Werkt op desktop, tablet en mobile

## Vereisten

- Node.js 18 of hoger
- npm 9 of hoger
- Angular CLI 17 (optioneel, maar aanbevolen)

## Installatie

```bash
# Installeer dependencies
npm install

# Installeer Angular CLI globally (optioneel)
npm install -g @angular/cli
```

## Development Server

Start de development server:

```bash
npm start
# of
ng serve
```

Navigeer naar `http://localhost:4200`. De applicatie herlaadt automatisch wanneer je source files wijzigt.

### Backend Verbinding

De frontend maakt standaard verbinding met de backend op `http://localhost:8080`.

Zorg dat de Quarkus backend draait voordat je de frontend start:

```bash
# In de hoofdmap van het project
mvn quarkus:dev
```

De proxy configuratie in `src/proxy.conf.json` zorgt ervoor dat `/api` requests worden doorgestuurd naar de backend.

## Bouwen voor Productie

### Lokale Build

```bash
# Production build
npm run build

# Output staat in dist/mtom-json-parser-frontend
```

### Docker Build

```bash
# Build Docker image
docker build -t mtom-json-parser-frontend .

# Run container
docker run -p 80:80 mtom-json-parser-frontend
```

De Dockerfile gebruikt een multi-stage build:
1. Stage 1: Bouwt de Angular applicatie
2. Stage 2: Served de app via Nginx

## Project Structuur

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── mtom-upload/        # Hoofdcomponent voor file upload
│   │   │   └── json-viewer/        # Component voor JSON weergave
│   │   ├── models/                 # TypeScript interfaces
│   │   │   ├── parsed-message.model.ts
│   │   │   ├── client-configuration.model.ts
│   │   │   └── error-response.model.ts
│   │   ├── services/
│   │   │   └── mtom-api.service.ts # HTTP service voor backend calls
│   │   ├── app.component.*         # Root component
│   │   └── app.module.ts           # App module
│   ├── assets/                     # Static assets
│   ├── environments/               # Environment configs
│   └── styles.scss                 # Global styles
├── angular.json                    # Angular CLI config
├── package.json                    # Dependencies
├── tsconfig.json                   # TypeScript config
├── Dockerfile                      # Production build
└── nginx.conf                      # Nginx config voor production
```

## Componenten

### MtomUploadComponent

Hoofdcomponent voor:
- File selectie en upload
- Client configuratie selectie
- Convert en Validate acties
- Error display
- Integration met JsonViewerComponent

### JsonViewerComponent

Toont geparseerde JSON in gestructureerd formaat:
- Expansion panels voor verschillende secties
- Metadata grid met alle velden
- Content informatie
- Raw JSON view
- Copy to clipboard functionaliteit

## Services

### MtomApiService

HTTP service voor communicatie met backend API:

- `convertMtom(mtomXml, clientId)`: Convert MTOM to JSON
- `validateMtom(mtomXml, clientId)`: Validate MTOM
- `getAllConfigurations()`: Get alle client configs
- `getConfiguration(clientId)`: Get specific config
- `saveConfiguration(config)`: Create/update config
- `updateConfiguration(clientId, config)`: Update config
- `deleteConfiguration(clientId)`: Delete config
- `reloadConfiguration(clientId)`: Reload config from storage

## Configuratie

### Environment Files

**development** (`src/environments/environment.ts`):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

**production** (`src/environments/environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: ''  // Same origin in production
};
```

### Proxy Configuration

Voor development wordt een proxy gebruikt om CORS te vermijden (`src/proxy.conf.json`):

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

## Styling

De applicatie gebruikt **Angular Material** voor UI components met het Indigo-Pink theme.

Globale styles in `src/styles.scss`
Component-specifieke styles in `*.component.scss`

## Testing

```bash
# Unit tests
npm test

# E2E tests (als geconfigureerd)
npm run e2e
```

## Linting

```bash
npm run lint
```

## Deployment

### OpenShift/Kubernetes

De applicatie kan worden gedeployed in OpenShift/Kubernetes met de meegeleverde Dockerfile.

Nginx configuratie (`nginx.conf`) bevat:
- Gzip compressie
- Security headers
- API proxy naar backend service
- SPA routing support
- Static asset caching

### Environment Variables in Container

De backend URL kan worden geconfigureerd via environment variables bij container deployment.

## Browser Support

- Chrome (laatste versies)
- Firefox (laatste versies)
- Safari (laatste versies)
- Edge (laatste versies)

## Troubleshooting

### Backend Connection Errors

Als je CORS errors of connection errors krijgt:

1. Controleer of de backend draait op `http://localhost:8080`
2. Controleer of de proxy configuratie correct is
3. Start de Angular dev server opnieuw

### Build Errors

Als `npm install` faalt:
```bash
# Clear cache en probeer opnieuw
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

## License

Copyright © 2025 Overheid ECM
