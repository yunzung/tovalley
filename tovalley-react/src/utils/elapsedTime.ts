export function elapsedTime(date: string) {
  const start = new Date(date);
  const end = new Date();

  const seconds = Math.floor((end.getTime() - start.getTime()) / 1000);
  if (seconds < 60) return "방금 전";

  const minutes = seconds / 60;
  if (minutes < 60) return `${Math.floor(minutes)}분 전`;

  const hours = minutes / 60;
  if (hours < 24) return `${Math.floor(hours)}시간 전`;

  const days = hours / 24;
  if (days < 7) return `${Math.floor(days)}일 전`;

  const parseMonth =
    start.getMonth() + 1 < 10
      ? `0${start.getMonth() + 1}`
      : start.getMonth() + 1;

  const parseDate =
    start.getDate() < 10 ? `0${start.getDate()}` : start.getDate();

  return `${parseMonth}.${parseDate}`;
}
