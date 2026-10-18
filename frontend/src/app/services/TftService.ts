import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MatchDetailResponse, PlayerProfile } from '../models/tft.models';

@Injectable({ providedIn: 'root' })
export class TftService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/tft';

  getProfile(gameName: string, tagLine: string): Observable<PlayerProfile> {
    return this.http.get<PlayerProfile>(
      `${this.apiUrl}/players/${encodeURIComponent(gameName)}/${encodeURIComponent(tagLine)}`
    );
  }

  getMatch(matchId: string, puuid: string): Observable<MatchDetailResponse> {
    return this.http.get<MatchDetailResponse>(
      `${this.apiUrl}/matches/${encodeURIComponent(matchId)}`,
      { params: { puuid } }
    );
  }
}