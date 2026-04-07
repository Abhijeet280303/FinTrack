import { useState, useEffect } from 'react';
import { analyticsApi } from '../services/api';
import ExpensePieChart from '../components/Charts/ExpensePieChart';
import IncomeExpenseBarChart from '../components/Charts/IncomeExpenseBarChart';
import SpendingTrendChart from '../components/Charts/SpendingTrendChart';

export default function AnalyticsPage() {
  const [analytics, setAnalytics] = useState(null);
  const [months, setMonths] = useState(6);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchAnalytics() {
      setLoading(true);
      try {
        const { data } = await analyticsApi.getMonthly(months);
        setAnalytics(data);
      } catch (err) {
        console.error('Failed to load analytics:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchAnalytics();
  }, [months]);

  if (loading) {
    return (
      <div className="space-y-6">
        {[1, 2, 3].map((i) => <div key={i} className="skeleton h-80 rounded-2xl" />)}
      </div>
    );
  }

  const savingsRate = analytics?.savingsRate || 0;

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Analytics</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">Deep dive into your financial data</p>
        </div>
        <select
          value={months}
          onChange={(e) => setMonths(parseInt(e.target.value))}
          className="px-3 py-2 rounded-xl border border-gray-200 dark:border-gray-600 bg-white dark:bg-gray-700 text-sm text-gray-700 dark:text-gray-300 focus:ring-2 focus:ring-primary-500 outline-none"
        >
          <option value={3}>Last 3 months</option>
          <option value={6}>Last 6 months</option>
          <option value={12}>Last 12 months</option>
        </select>
      </div>

      {/* Summary Cards */}
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white dark:bg-gray-800 rounded-2xl p-5 shadow-card">
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Total Income</p>
          <p className="text-xl font-bold text-green-600">
            ₹{parseFloat(analytics?.totalIncome || 0).toLocaleString('en-IN')}
          </p>
        </div>
        <div className="bg-white dark:bg-gray-800 rounded-2xl p-5 shadow-card">
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Total Expenses</p>
          <p className="text-xl font-bold text-red-600">
            ₹{parseFloat(analytics?.totalExpenses || 0).toLocaleString('en-IN')}
          </p>
        </div>
        <div className="bg-white dark:bg-gray-800 rounded-2xl p-5 shadow-card">
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Total Savings</p>
          <p className="text-xl font-bold text-primary-600">
            ₹{parseFloat(analytics?.totalSavings || 0).toLocaleString('en-IN')}
          </p>
        </div>
        <div className="bg-white dark:bg-gray-800 rounded-2xl p-5 shadow-card">
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Savings Rate</p>
          <p className={`text-xl font-bold ${savingsRate >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {savingsRate.toFixed(1)}%
          </p>
        </div>
      </div>

      {/* Charts */}
      <div className="grid lg:grid-cols-2 gap-6">
        <div className="bg-white dark:bg-gray-800 rounded-2xl p-6 shadow-card">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Category Breakdown</h3>
          <ExpensePieChart data={analytics?.categoryBreakdown || {}} />
        </div>
        <div className="bg-white dark:bg-gray-800 rounded-2xl p-6 shadow-card">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Income vs Expenses</h3>
          <IncomeExpenseBarChart data={analytics?.monthlyTrends || []} />
        </div>
      </div>

      <div className="bg-white dark:bg-gray-800 rounded-2xl p-6 shadow-card">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Spending Trend</h3>
        <SpendingTrendChart data={analytics?.monthlyTrends || []} />
      </div>
    </div>
  );
}
