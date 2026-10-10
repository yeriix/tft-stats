import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TftService } from '../../services/TftService';

@Component({
  selector: 'app-search',
  imports: [FormsModule],
  templateUrl: './search.html',
  styleUrl: './search.scss',
})
export class Search {
  private tftService = inject(TftService);

  gameName = '';
  tagLine = '';

  matches = signal<any[]>([]);
  loading = signal(false);
  error = signal('');

  search() {
    this.loading.set(true);
    this.error.set('');
    this.matches.set([]);

    this.tftService.getMatches(this.gameName, this.tagLine).subscribe({
      next: (data) => {
        this.matches.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.detail ?? 'Unexpected error');
        this.loading.set(false);
      },
    });
  }
}