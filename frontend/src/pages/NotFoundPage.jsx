import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 px-4">
      <div className="text-center animate-slide-up">
        <div className="w-24 h-24 mx-auto mb-6 rounded-3xl bg-primary-50 dark:bg-primary-900/20 flex items-center justify-center">
          <span className="text-5xl">🔍</span>
        </div>
        <h1 className="text-6xl font-extrabold text-gray-200 dark:text-gray-700 mb-2">404</h1>
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">Page Not Found</h2>
        <p className="text-gray-500 dark:text-gray-400 mb-8 max-w-sm mx-auto">
          The page you&apos;re looking for doesn&apos;t exist or has been moved.
        </p>
        <Link
          to="/"
          className="inline-block px-6 py-3 bg-primary-600 text-white rounded-xl font-medium text-sm hover:bg-primary-700 transition-colors shadow-md"
        >
          Back to Home
        </Link>
      </div>
    </div>
  );
}
