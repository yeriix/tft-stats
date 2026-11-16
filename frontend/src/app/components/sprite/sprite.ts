import { Component, input } from '@angular/core';
import { SpriteRef } from '../../models/tft.models';

@Component({
  selector: 'app-sprite',
  templateUrl: './sprite.html',
  styleUrl: './sprite.scss',
})
export class Sprite {
  icon = input<SpriteRef | null>(null);
  size = input(48);
  label = input('');
}