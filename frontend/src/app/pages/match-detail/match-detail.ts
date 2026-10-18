import { Location } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { combineLatest } from 'rxjs';
import { MatchDetailResponse } from '../../models/tft.models';
import { TftService } from '../../services/tft.services';

import {
  formatDate,
  formatDuration,
  itemsLabel,
  placementClass,
  prettyName,
  stars,
} from '../../utils/format';

@Component({
  selector: 'app-match-detail',
  imports: [],
  templateUrl: './match-detail.html',
  styleUrl: './match-detail.scss',
})
export class MatchDetail {
  private route = inject(ActivatedRoute);
  private tftService = inject(TftService);
  private location = inject(Location);

  match = signal<MatchDetailResponse | null>(null);
  loading = signal(true);
  error = signal('');

  readonly prettyName = prettyName;
  readonly itemsLabel = itemsLabel;
  readonly stars = stars;
  readonly placementClass = placementClass;
  readonly formatDate = formatDate;
  readonly formatDuration = formatDuration;

  constructor() {
    combineLatest([this.route.paramMap, this.route.queryParamMap])
      .pipe(takeUntilDestroyed())
      .subscribe(([params, query]) => {
        this.load(params.get('matchId') ?? '', query.get('puuid') ?? '');
      });
  }

  goBack() {
    this.location.back();
  }

  private load(matchId: string, puuid: string) {
    this.loading.set(true);
    this.error.set('');
    this.match.set(null);

    this.tftService.getMatch(matchId, puuid).subscribe({
      next: (match) => {
        this.match.set(match);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.detail ?? 'Unexpected error');
        this.loading.set(false);
      },
    });
  }
}