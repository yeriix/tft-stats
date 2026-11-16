import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UnitTile } from './unit-tile';

describe('UnitTile', () => {
  let component: UnitTile;
  let fixture: ComponentFixture<UnitTile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnitTile],
    }).compileComponents();

    fixture = TestBed.createComponent(UnitTile);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
