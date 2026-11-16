import { Component, input } from '@angular/core';
import { Unit } from '../../models/tft.models';
import { stars } from '../../utils/format';
import { Sprite } from '../sprite/sprite';

@Component({
  selector: 'app-unit-tile',
  imports: [Sprite],
  templateUrl: './unit-tile.html',
  styleUrl: './unit-tile.scss',
})
export class UnitTile {
  unit = input.required<Unit>();
  readonly stars = stars;
}