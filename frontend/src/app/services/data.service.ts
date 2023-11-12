import { Injectable, } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { of, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private page = 0;
  private size = 50;
  private backendUrl = "http://localhost:8080"
  private cities = [];

  private lastPageReached: BehaviorSubject<any> = new BehaviorSubject<any>([]);

  constructor(private http: HttpClient) { }

  public loadData() {
    this.http.get(`${this.backendUrl}/cities?page=${this.page}&size=${this.size}&shouldIncludeDensity=true`).subscribe((data: any) => {
      this.cities = this.cities.concat(data["content"]);

      if (!data["last"]) {
        this.page++;
        this.loadData();
      }
      else {
        this.lastPageReached.next(this.cities);
      }
    });
  }

  public getLastPageReached(): any {
    return this.lastPageReached.asObservable();
  }
}