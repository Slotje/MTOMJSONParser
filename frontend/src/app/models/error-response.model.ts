export interface ErrorResponse {
  errorCode: string;
  errorMessage: string;
  errorDetails?: { [key: string]: any };
  timestamp: string;
  messageId?: string;
  clientId?: string;
  retryable?: boolean;
}
