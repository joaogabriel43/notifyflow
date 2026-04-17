import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private isDark = signal(false);
  
  toggle() {
    this.isDark.update(v => !v);
    document.documentElement.setAttribute(
      'data-theme', 
      this.isDark() ? 'dark' : 'light'
    );
    localStorage.setItem('theme', this.isDark() ? 'dark' : 'light');
  }
  
  init() {
    const saved = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const dark = saved === 'dark' || (!saved && prefersDark);
    this.isDark.set(dark);
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light');
  }
  
  isDarkMode = computed(() => this.isDark());
}
