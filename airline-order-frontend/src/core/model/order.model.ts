export interface Order {
  id: number;
  orderNumber: string;
  status: string;
  amount: number;        // 我们要转成 number
  creationDate: Date;    // 要转成 Date 对象
  user: {
    id: number;
    username: string;
  };
}