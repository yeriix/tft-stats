import { Routes } from '@angular/router';
import { MatchDetail } from './pages/match-detail/match-detail';
import { Player } from './pages/player/player';
import { Search } from './pages/search/search';

export const routes: Routes = [
  { path: '', component: Search },
  { path: 'player/:gameName/:tagLine', component: Player },
  { path: 'match/:matchId', component: MatchDetail },
  { path: '**', redirectTo: '' },
];