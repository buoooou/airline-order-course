import { HttpParams } from '@angular/common/http';
import { Observable, throwError, timer } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { HTTP_CONFIG } from '../services/http.config';

/**
 * Builds query parameters
 */
export function buildQueryParams(params: Record<string, any>): HttpParams {
  let httpParams = new HttpParams();
  
  Object.keys(params).forEach(key => {
    const value = params[key];
    
    if (value !== null && value !== undefined && value !== '') {
      if (Array.isArray(value)) {
        // 处理数组参数
        value.forEach(item => {
          httpParams = httpParams.append(key, item.toString());
        });
      } else if (typeof value === 'object') {
        // 处理对象参数（转换为JSON字符串）
        httpParams = httpParams.set(key, JSON.stringify(value));
      } else {
        // 处理基本类型参数
        httpParams = httpParams.set(key, value.toString());
      }
    }
  });

  return httpParams;
}

/**
 * 验证文件上传
 */
export function validateFile(file: File, config: {
  maxSize?: number;
  allowedTypes?: string[];
} = {}): { valid: boolean; message?: string } {
  const { 
    maxSize = HTTP_CONFIG.upload.maxSize, 
    allowedTypes = HTTP_CONFIG.upload.allowedTypes 
  } = config;

  // 检查文件大小
  if (maxSize && file.size > maxSize) {
    return {
      valid: false,
      message: `文件大小不能超过 ${formatFileSize(maxSize)}`
    };
  }

  // 检查文件类型
  if (allowedTypes.length > 0 && !allowedTypes.some(type => {
    // 支持通配符匹配，如 image/*
    if (type.endsWith('/*')) {
      const prefix = type.slice(0, -1);
      return file.type.startsWith(prefix);
    }
    return type === file.type;
  })) {
    const supportedTypes = allowedTypes.join(', ');
    return {
      valid: false,
      message: `不支持的文件类型: ${file.type || '未知类型'}。支持的格式: ${supportedTypes}`
    };
  }

  return { valid: true };
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * 防抖函数
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: ReturnType<typeof setTimeout>;
  
  return (...args: Parameters<T>) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(null, args), wait);
  };
}

/**
 * 节流函数
 */
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  limit: number
): (...args: Parameters<T>) => void {
  let inThrottle: boolean;
  
  return (...args: Parameters<T>) => {
    if (!inThrottle) {
      func.apply(null, args);
      inThrottle = true;
      setTimeout(() => inThrottle = false, limit);
    }
  };
}

/**
 * 重试逻辑
 */
export function retry<T>(
  operation: () => Observable<T>,
  maxRetries: number = HTTP_CONFIG.api.retryCount,
  delay: number = HTTP_CONFIG.api.retryDelay
): Observable<T> {
  return operation().pipe(
    catchError((error, caught) => {
      if (maxRetries > 0) {
        return timer(delay).pipe(
          switchMap(() => retry(operation, maxRetries - 1, delay * 2))
        );
      }
      return throwError(error);
    })
  );
}

/**
 * 生成唯一请求ID
 */
export function generateRequestId(): string {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
}

/**
 * 检查网络连接状态
 */
export function checkNetworkStatus(): Promise<boolean> {
  return new Promise((resolve) => {
    if (navigator.onLine) {
      // 进一步验证网络连接
      fetch('/favicon.ico', { method: 'HEAD', cache: 'no-cache' })
        .then(() => resolve(true))
        .catch(() => resolve(false));
    } else {
      resolve(false);
    }
  });
}

/**
   * 缓存工具
   */
  export class HttpCache {
    private cache = new Map<string, { data: any; timestamp: number; ttl: number }>();

    set(key: string, data: any, ttl: number = HTTP_CONFIG.api.timeout): void {
    this.cache.set(key, {
      data,
      timestamp: Date.now(),
      ttl
    });
  }

  get(key: string): any | null {
    const cached = this.cache.get(key);
    if (!cached) return null;

    if (Date.now() - cached.timestamp > cached.ttl) {
      this.cache.delete(key);
      return null;
    }

    return cached.data;
  }

  clear(): void {
    this.cache.clear();
  }

  delete(key: string): boolean {
    return this.cache.delete(key);
  }
}