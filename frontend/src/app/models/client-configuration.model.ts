export interface ClientConfiguration {
  clientId: string;
  clientName: string;
  metadataMapping: MetadataMapping;
  businessInfo: BusinessInfo;
  processingRules?: ProcessingRules;
}

export interface MetadataMapping {
  objectStore: string;
  documentClass: string;
  fields: FieldMapping[];
}

export interface FieldMapping {
  sourceField: string;
  targetProperty: string;
  required: boolean;
  format?: string;
  defaultValue?: string;
  multiValue?: boolean;
}

export interface BusinessInfo {
  contactEmail: string;
  supportGroup: string;
  maxMessageSize?: number;
  maxMessagesPerDay?: number;
}

export interface ProcessingRules {
  retentionDays?: number;
  autoRetryEnabled?: boolean;
  processingEnabled?: boolean;
}
