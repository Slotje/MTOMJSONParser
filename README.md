# MTOM to JSON Parser

Een OpenShift Quarkus applicatie voor het converteren van MTOM (Message Transmission Optimization Mechanism) berichten naar gestandaardiseerd JSON formaat met uitgebreide validatie.

## Overzicht

Deze applicatie is ontwikkeld als onderdeel van de BTE (Berichtenverwerking en Transformatie Engine) om MTOM-berichten van klanten te converteren naar een gestandaardiseerd JSON-formaat. Het systeem ondersteunt:

- **MTOM Parsing**: Extractie van metadata uit MTOM XML-berichten op basis van configureerbare XPath mappings
- **JSON Conversie**: Transformatie naar een gestandaardiseerd JSON-formaat
- **JSON Schema Validatie**: Validatie van zowel klantconfiguraties als geparseerde berichten
- **Business Rules Validatie**: Validatie van berichtgrootte, verplichte velden en andere business rules
- **Gestructureerde Foutafhandeling**: Duidelijke foutcategorieën met retryable flags
- **Configuratiebeheer**: Dynamisch beheer van klantconfiguraties via REST API

## Architectuur

De applicatie bestaat uit de volgende componenten:

### Parsing Pipeline

1. **Berichtdetectie**: Identificatie van het berichtformaat en de klant
2. **Configuratie Ophalen**: Ophalen van klantspecifieke metadata mappings
3. **Metadata Extractie**: XPath-gebaseerde extractie van metadata uit MTOM XML
4. **JSON Constructie**: Samenstellen van gestandaardiseerd JSON-object
5. **Validatie**: JSON Schema en business rule validatie
6. **Output**: Succesvol bericht of gestructureerde foutmelding

### Foutcategorieën

- **PARSE_xxx**: Structuurfouten in MTOM berichten (HTTP 400)
- **CONFIG_xxx**: Configuratiefouten (HTTP 500, interne fout)
- **VALID_xxx**: Validatiefouten (HTTP 400)
- **TECH_xxx**: Technische fouten (HTTP 500)

## Technologie Stack

### Backend
- **Quarkus 3.6.4**: Java framework voor cloud-native applicaties
- **Java 17**: Programmeertaal
- **OpenShift**: Container orchestration platform
- **JSON Schema Validator**: Voor JSON schema validatie
- **RESTEasy Reactive**: Voor REST API endpoints
- **Hibernate Validator**: Voor bean validatie
- **SmallRye Health**: Voor health checks
- **Micrometer Prometheus**: Voor metrics

### Frontend
- **Angular 17**: Modern web framework
- **Angular Material**: UI component library
- **TypeScript**: Type-safe JavaScript
- **RxJS**: Reactive programming library
- **Nginx**: Web server voor production deployment

## Frontend Applicatie

De Angular frontend biedt een gebruiksvriendelijke interface voor het uploaden en converteren van MTOM bestanden.

### Features
- 📤 **File Upload**: Drag-and-drop MTOM XML bestanden
- 🔍 **Client Selectie**: Kies de juiste klantconfiguratie
- ✅ **Validatie**: Valideer MTOM berichten voordat conversie
- 📊 **JSON Viewer**: Gestructureerde weergave van geparseerde metadata
- 💾 **Download**: Download geparseerde JSON
- ⚠️ **Error Display**: Duidelijke foutmeldingen met details

### Frontend Starten

```bash
cd frontend

# Dependencies installeren
npm install

# Development server starten
npm start
```

De applicatie is beschikbaar op `http://localhost:4200`

De frontend maakt verbinding met de backend API op `http://localhost:8080` (configureerbaar in `src/environments/environment.ts`).

### Frontend Bouwen voor Productie

```bash
cd frontend

# Production build
npm run build

# Docker image bouwen
docker build -t mtom-json-parser-frontend .

# Container starten
docker run -p 80:80 mtom-json-parser-frontend
```

## API Endpoints

### MTOM Conversie

#### Convert MTOM to JSON
```
POST /api/v1/mtom/convert?clientId={clientId}
Content-Type: application/xml

<MTOM XML body>
```

Response (200 OK):
```json
{
  "messageId": "550e8400-e29b-41d4-a716-446655440001",
  "clientId": "550e8400-e29b-41d4-a716-446655440000",
  "metadata": {
    "BBM1": "550e8400-e29b-41d4-a716-446655440001",
    "BBM2": "CA",
    "BBM3": "BTE",
    "Documentsoort": "Brief"
  },
  "content": {
    "data": "base64-encoded-content",
    "mimeType": "application/pdf",
    "filename": "example.pdf",
    "size": 12345
  },
  "parsingInfo": {
    "timestamp": "2025-12-22T12:00:00Z",
    "parserVersion": "1.0.0",
    "processingTimeMs": 150
  }
}
```

#### Validate MTOM Message
```
POST /api/v1/mtom/validate?clientId={clientId}
Content-Type: application/xml

<MTOM XML body>
```

### Configuratiebeheer

#### Get All Configurations
```
GET /api/v1/config
```

#### Get Client Configuration
```
GET /api/v1/config/{clientId}
```

#### Create/Update Configuration
```
POST /api/v1/config
Content-Type: application/json

{
  "clientId": "550e8400-e29b-41d4-a716-446655440000",
  "clientName": "Example Client",
  "metadataMapping": { ... },
  "businessInfo": { ... },
  "processingRules": { ... }
}
```

#### Delete Configuration
```
DELETE /api/v1/config/{clientId}
```

#### Reload Configuration
```
POST /api/v1/config/{clientId}/reload
```

#### Reload All Configurations
```
POST /api/v1/config/reload-all
```

### Health & Metrics

- **Liveness**: `GET /q/health/live`
- **Readiness**: `GET /q/health/ready`
- **Metrics**: `GET /q/metrics`

## Klantconfiguratie

Een klantconfiguratie bestaat uit drie hoofdonderdelen:

### 1. Metadata Mapping

Definieert hoe velden uit MTOM berichten worden gemapt naar FileNet properties:

```json
{
  "objectStore": "ECM_ObjectStore",
  "documentClass": "Document",
  "fields": [
    {
      "sourceField": "//ecmid",
      "targetProperty": "BBM1",
      "required": true,
      "multiValue": false
    },
    {
      "sourceField": "//value[@key='Eigenaar_L1']",
      "targetProperty": "BBM2",
      "required": true,
      "multiValue": false
    }
  ]
}
```

### 2. Business Info

Bevat klantgegevens en limieten:

```json
{
  "contactEmail": "support@example.nl",
  "supportGroup": "ECM Support Team",
  "maxMessageSize": 10485760,
  "maxMessagesPerDay": 10000
}
```

### 3. Processing Rules

Verwerkingsinstellingen:

```json
{
  "retentionDays": 30,
  "autoRetryEnabled": true,
  "processingEnabled": true
}
```

Zie `src/main/resources/example-client-config.json` voor een volledig voorbeeld.

## Lokale Ontwikkeling

### Vereisten

- Java 17+
- Maven 3.8+
- Docker (optioneel, voor containerized deployment)

### Development Mode

Start de applicatie in development mode met live reload:

```bash
mvn quarkus:dev
```

De applicatie is beschikbaar op `http://localhost:8080`

### Tests Uitvoeren

```bash
mvn test
```

### Package Applicatie

```bash
mvn package
```

Dit genereert een uber-jar in `target/quarkus-app/`.

## OpenShift Deployment

### Build Image

```bash
mvn package
docker build -f src/main/docker/Dockerfile.jvm -t mtom-json-parser:1.0.0-SNAPSHOT .
```

### Deploy naar OpenShift

```bash
oc apply -f src/main/kubernetes/openshift.yml
```

Dit deployt:
- Deployment met 2 replicas
- Service (ClusterIP)
- Route met TLS
- PersistentVolumeClaim voor configuratie storage

### Configuratie Management in OpenShift

Klantconfiguraties worden opgeslagen in een PersistentVolume op `/opt/app/config/clients`.

Om een configuratie toe te voegen:

```bash
# Copy configuratie naar pod
oc cp example-client-config.json <pod-name>:/opt/app/config/clients/550e8400-e29b-41d4-a716-446655440000.json

# Reload configuratie via API
curl -X POST http://<route>/api/v1/config/reload-all
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `QUARKUS_LOG_LEVEL` | `INFO` | Log level |
| `client.config.storage.path` | `/opt/app/config/clients` | Path voor klantconfiguraties |
| `mtom.parser.max-message-size` | `10485760` | Max berichtgrootte in bytes |
| `mtom.parser.max-retry-attempts` | `3` | Max aantal retry pogingen |

## Voorbeeld Gebruik

### 1. Configuratie Aanmaken

```bash
curl -X POST http://localhost:8080/api/v1/config \
  -H "Content-Type: application/json" \
  -d @src/main/resources/example-client-config.json
```

### 2. MTOM Bericht Converteren

```bash
curl -X POST "http://localhost:8080/api/v1/mtom/convert?clientId=550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/xml" \
  -d @src/main/resources/example-mtom-message.xml
```

### 3. MTOM Bericht Valideren

```bash
curl -X POST "http://localhost:8080/api/v1/mtom/validate?clientId=550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/xml" \
  -d @src/main/resources/example-mtom-message.xml
```

## Foutafhandeling

De applicatie retourneert gestructureerde foutmeldingen:

```json
{
  "errorCode": "VALID_001",
  "errorMessage": "Required field missing",
  "errorDetails": {
    "validationErrors": [
      "Required metadata field missing: BBM2 (source: //value[@key='Eigenaar_L1'])"
    ]
  },
  "timestamp": "2025-12-22T12:00:00Z",
  "messageId": "550e8400-e29b-41d4-a716-446655440001",
  "clientId": "550e8400-e29b-41d4-a716-446655440000",
  "retryable": true
}
```

## Monitoring

### Health Checks

```bash
# Liveness
curl http://localhost:8080/q/health/live

# Readiness
curl http://localhost:8080/q/health/ready
```

### Metrics

```bash
curl http://localhost:8080/q/metrics
```

Prometheus metrics zijn beschikbaar voor monitoring van:
- Request counts en durations
- Parser performance
- Validation succes/failure rates
- Configuration cache hits

## Licentie

Copyright © 2025 Overheid ECM
