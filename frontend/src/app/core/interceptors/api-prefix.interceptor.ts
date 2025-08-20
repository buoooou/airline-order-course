import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const ApiPrefixInterceptor: HttpInterceptorFn = (req, next) => {
  const apiReq = req.clone({ url: `${environment.apiUrl}${req.url}` });
  console.log('ApiPrefixInterceptor:' + apiReq);
  return next(apiReq);
};
