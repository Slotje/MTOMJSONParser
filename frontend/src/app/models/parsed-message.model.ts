export interface ParsedMessage {
  messageId: string;
  clientId: string;
  metadata: { [key: string]: any };
  content: MessageContent;
  parsingInfo: ParsingInfo;
}

export interface MessageContent {
  data: string;
  mimeType: string;
  filename?: string;
  size?: number;
}

export interface ParsingInfo {
  timestamp: string;
  parserVersion: string;
  processingTimeMs?: number;
}
