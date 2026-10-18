import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search',
  imports: [FormsModule],
  templateUrl: './search.html',
  styleUrl: './search.scss',
})
export class Search {
  private router = inject(Router);

  riotId = '';
  error = signal('');

  search() {
    const value = this.riotId.trim();
    const hash = value.lastIndexOf('#');

    if (hash < 1 || hash === value.length - 1) {
      this.error.set('Enter a Riot ID like Name#TAG');
      return;
    }

    this.error.set('');
    this.router.navigate([
      '/player',
      value.slice(0, hash).trim(),
      value.slice(hash + 1).trim(),
    ]);
  }
}