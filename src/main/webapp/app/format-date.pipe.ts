import { Pipe, PipeTransform } from '@angular/core';
import dayjs, { Dayjs } from 'dayjs';

@Pipe({
  name: 'formatDate',
  standalone: true
})
export class FormatDatePipe implements PipeTransform {
  transform(value: Dayjs | string | Date | null | undefined): string {
    if (!value) {
      return 'NULL';
    }

    // Se for um objeto Dayjs, formata diretamente
    if (dayjs.isDayjs(value)) {
      return value.format('DD/MM/YYYY');
    }

    // Se for uma string ou Date, converte para Dayjs e formata
    return dayjs(value).format('DD/MM/YYYY');
  }
}
