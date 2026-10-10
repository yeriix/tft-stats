import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TftService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/tft';

  getMatches(gameName: string, tagLine: string, count = 5): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.apiUrl}/players/${encodeURIComponent(gameName)}/${encodeURIComponent(tagLine)}/matches`,
      { params: { count } }
    );
  }
}