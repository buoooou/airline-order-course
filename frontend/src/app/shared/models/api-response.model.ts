export interface ApiResponseDTO<T> {
    code: number;
    message: string;
    data: T;
}