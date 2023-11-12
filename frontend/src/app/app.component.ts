import { Component, OnDestroy, OnInit } from '@angular/core';
import { DataService } from './services/data.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  public title = "frontend"
  public cities = [];
  private subscription: Subscription = new Subscription();

  constructor(private dataService: DataService) { }

  ngOnInit() {
    this.dataService.loadData();
    this.subscription = this.dataService.getLastPageReached().subscribe((cities: any) => {
      this.cities = cities;
      console.log(this.cities);
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

}
