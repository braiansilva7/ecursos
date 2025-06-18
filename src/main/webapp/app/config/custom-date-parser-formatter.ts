import { Injectable } from '@angular/core';
import { NgbDateParserFormatter, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import dayjs from 'dayjs/esm';

@Injectable()
export class CustomDateParserFormatter extends NgbDateParserFormatter {

  // Transformando uma string em um NgbDateStruct
  parse(value: string): NgbDateStruct | null {
    if (value) {
      const date = dayjs(value, 'DD/MM/YYYY');
      return { day: date.date(), month: date.month() + 1, year: date.year() };
    }
    return null;
  }

  // Transformando um NgbDateStruct em uma string formatada
  format(date: NgbDateStruct | null): string {
    return date ? dayjs(`${date.year}-${date.month}-${date.day}`).format('DD/MM/YYYY') : '';
  }
}