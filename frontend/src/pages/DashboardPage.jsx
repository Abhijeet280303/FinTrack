import { useState, useEffect } from 'react';
import { dashboardApi } from '../services/api';
import ExpensePieChart from '../components/Charts/ExpensePieChart';
import IncomeExpenseBarChart from '../components/Charts/IncomeExpenseBarChart';
import SpendingTrendChart from '../components/Charts/SpendingTrendChart';
import { analyticsApi } from '../services/api';
import {
  BanknotesIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  WalletIcon,
} from '@heroicons/react/24/outline';

const summaryCards = [
  { key: 'totalBalance',   label: 'Total Balance',    icon: WalletIcon,           color: 'primary' },
  { key: 'monthlyIncome',  label: 'Monthly Income',   icon: ArrowTrendingUpIcon,  color: 'green' },
  { key: 'monthlyExpenses',label: 'Monthly Expenses',  icon: ArrowTrendingDownIcon,color: 'red' },
  { key: 'savings',        label: 'Monthly Savings',  icon: BanknotesIcon,        color: 'amber' },
];

const colorMap = {
  primary: 'bg-primary-50 dark:bg-primary-900/20 text-primary-600 dark:text-primary-400',
  green:   'bg-green-50 dark:bg-green-900/20 text-green-600 dark:text-green-400',
  red:     'bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400',
  amber:   'bg-amber-50 dark:bg-amber-900/20 text-amber-600 dark:text-amber-400',
};

export default function DashboardPage() {
  const [dashboard, setDashboard] = useState(null);
  const [trends, setTrends] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const [dashRes, trendRes] = await Promise.all([
          dashboardApi.get(),
          analyticsApi.getMonthly(6),
        ]);
        setDashboard(dashRes.data);
        setTrends(trendRes.data.monthlyTrends || []);
      } catch (err) {
        console.error('Failed to load dashboard:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="skeleton h-28 rounded-2xl" />
          ))}
        </div>
        <div className="grid lg:grid-cols-2 gap-6">
          <div className="skeleton h-80 rounded-2xl" />
          <div className="skeleton h-80 rounded-2xl" />
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Summary Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {summaryCards.map(({ key, label, icon: Icon, color }) => (
          <div
            key={key}
            className="bg-white dark:bg-gray-800 rounded-2xl p-5 shadow-card card-interactive"
          >
            <div className="flex items-center justify-between mb-3">
              <span className="text-sm text-gray-500 dark:text-gray-400 font-medium">{label}</span>
              <div className={`p-2 rounded-xl ${colorMap[color]}`}>
                <Icon className="w-5 h-5" />
              </div>
            </div>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">
              ₹{parseFloat(dashboard?.[key] || 0).toLocaleString('en-IN')}
            </p>
          </div>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid lg:grid-cols-2 gap-6">
        <div className="bg-white dark:bg-gray-800 rounded-2xl p-6 shadow-card">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Expense Breakdown</h3>
          <ExpensePieChart data={dashboard?.expensesByCategory || {}} />
        </div>
        <div className="bg-white dark:bg-gray-800 rounded-2xl p-6 shadow-card">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Income vs Expenses</h3>
          <IncomeExpenseBarChart data={trends} />
        </div>
      </div>

      {/* Spending Trend */}
      <div className="bg-white dark:bg-gray-800 rounded-2xl p-6 shadow-card">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Spending Trend</h3>
        <SpendingTrendChart data={trends} />
      </div>

      {/* Recent Transactions */}
      <div className="bg-white dark:bg-gray-800 rounded-2xl p-6 shadow-card">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Recent Transactions</h3>
        {(dashboard?.recentTransactions || []).length === 0 ? (
          <p className="text-gray-400 dark:text-gray-500 text-sm">No transactions yet</p>
        ) : (
          <div className="space-y-3">
            {dashboard.recentTransactions.map((txn) => (
              <div
                key={`${txn.type}-${txn.id}`}
                className="flex items-center justify-between py-3 border-b border-gray-100 dark:border-gray-700 last:border-0"
              >
                <div className="flex items-center gap-3">
                  <div
                    className={`w-10 h-10 rounded-xl flex items-center justify-center text-sm font-bold ${
                      txn.type === 'INCOME'
                        ? 'bg-green-50 dark:bg-green-900/20 text-green-600'
                        : 'bg-red-50 dark:bg-red-900/20 text-red-600'
                    }`}
                  >
                    {txn.type === 'INCOME' ? '+' : '−'}
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-900 dark:text-white">{txn.description || txn.category}</p>
                    <p className="text-xs text-gray-400">{txn.date} • {txn.category}</p>
                  </div>
                </div>
                <span
                  className={`text-sm font-semibold ${
                    txn.type === 'INCOME' ? 'text-green-600' : 'text-red-600'
                  }`}
                >
                  {txn.type === 'INCOME' ? '+' : '−'}₹{parseFloat(txn.amount).toLocaleString('en-IN')}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
