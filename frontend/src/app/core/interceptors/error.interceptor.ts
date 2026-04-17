import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMsg = 'An unknown error occurred';
      if (error.error instanceof ErrorEvent) {
        // Client side error
        errorMsg = error.error.message;
      } else {
        // Server side error
        if (error.error?.detail) {
          errorMsg = error.error.detail;
        } else if (error.error?.title) {
          errorMsg = error.error.title;
        } else {
          errorMsg = `Error Code: ${error.status},  Message: ${error.message}`;
        }
      }
      
      snackBar.open(errorMsg, 'Close', {
        duration: 5000,
        panelClass: ['error-snackbar']
      });

      return throwError(() => new Error(errorMsg));
    })
  );
};
