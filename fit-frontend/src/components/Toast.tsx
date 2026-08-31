import { useState, useEffect, useCallback } from 'react';

interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info';
}

let addToastFn: ((message: string, type: Toast['type']) => void) | null = null;

export function toast(message: string, type: Toast['type'] = 'info') {
  addToastFn?.(message, type);
}

export function ToastContainer() {
  const [toasts, setToasts] = useState<Toast[]>([]);
  let nextId = 0;

  const addToast = useCallback((message: string, type: Toast['type']) => {
    const id = ++nextId;
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 3000);
  }, []);

  useEffect(() => {
    addToastFn = addToast;
    return () => { addToastFn = null; };
  }, [addToast]);

  const bgColor = (type: Toast['type']) => {
    switch (type) {
      case 'success': return 'var(--positive)';
      case 'error': return '#FF3B30';
      default: return 'var(--accent)';
    }
  };

  return (
    <div className="fixed top-4 left-1/2 -translate-x-1/2 z-50 flex flex-col gap-2 w-80 max-w-[90vw]">
      {toasts.map((t) => (
        <div
          key={t.id}
          className="px-4 py-3 rounded-xl text-white text-sm font-medium shadow-lg animate-in"
          style={{ backgroundColor: bgColor(t.type) }}
        >
          {t.message}
        </div>
      ))}
    </div>
  );
}