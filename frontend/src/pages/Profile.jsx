import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';
import {
  UserCircleIcon,
  EnvelopeIcon,
  PhoneIcon,
  AcademicCapIcon,
  BuildingOfficeIcon,
  BriefcaseIcon,
  PencilIcon,
  CheckIcon,
  XMarkIcon
} from '@heroicons/react/24/outline';

const Profile = () => {
  const { user, login } = useAuth();
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    phone: '',
    batch: '',
    department: '',
    targetPackage: '',
  });

  useEffect(() => {
    if (user) {
      setForm({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        phone: user.phone || '',
        batch: user.batch || '',
        department: user.department || '',
        targetPackage: user.targetPackage || '',
      });
    }
  }, [user]);

  const handleSave = async () => {
    setSaving(true);
    try {
      const updated = await authAPI.updateProfile(form);
      const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
      const newUser = { ...storedUser, ...updated };
      localStorage.setItem('user', JSON.stringify(newUser));
      setMessage('Profile updated successfully!');
      setEditing(false);
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setMessage('Failed to update profile.');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setForm({
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      phone: user.phone || '',
      batch: user.batch || '',
      department: user.department || '',
      targetPackage: user.targetPackage || '',
    });
    setEditing(false);
  };

  const Field = ({ label, icon: Icon, field, type = 'text' }) => (
    <div>
      <label className="block text-sm font-medium text-gray-600 mb-1">{label}</label>
      <div className="flex items-center space-x-2">
        <Icon className="w-5 h-5 text-gray-400 flex-shrink-0" />
        {editing ? (
          <input
            type={type}
            value={form[field]}
            onChange={e => setForm({ ...form, [field]: e.target.value })}
            className="input flex-1"
          />
        ) : (
          <span className="text-gray-900">{user?.[field] || '—'}</span>
        )}
      </div>
    </div>
  );

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      {/* Header */}
      <div className="bg-gradient-to-r from-primary-600 to-primary-800 rounded-2xl p-8 text-white flex items-center space-x-6">
        <div className="w-20 h-20 rounded-full bg-white/20 flex items-center justify-center text-3xl font-bold">
          {user?.firstName?.charAt(0)}{user?.lastName?.charAt(0)}
        </div>
        <div>
          <h1 className="text-2xl font-bold">{user?.firstName} {user?.lastName}</h1>
          <p className="text-primary-100">{user?.email}</p>
          <span className="mt-2 inline-block bg-white/20 text-white text-xs font-semibold px-3 py-1 rounded-full">
            {user?.role || 'STUDENT'}
          </span>
        </div>
      </div>

      {message && (
        <div className={`p-3 rounded-lg text-sm font-medium ${message.includes('success') ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
          {message}
        </div>
      )}

      {/* Profile Card */}
      <div className="card">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold text-gray-900">Personal Information</h2>
          {!editing ? (
            <button onClick={() => setEditing(true)} className="flex items-center space-x-1 text-primary-600 hover:text-primary-700 text-sm font-medium">
              <PencilIcon className="w-4 h-4" />
              <span>Edit</span>
            </button>
          ) : (
            <div className="flex space-x-2">
              <button onClick={handleSave} disabled={saving} className="flex items-center space-x-1 bg-primary-600 text-white px-3 py-1.5 rounded-lg text-sm font-medium hover:bg-primary-700 disabled:opacity-50">
                <CheckIcon className="w-4 h-4" />
                <span>{saving ? 'Saving…' : 'Save'}</span>
              </button>
              <button onClick={handleCancel} className="flex items-center space-x-1 bg-gray-100 text-gray-600 px-3 py-1.5 rounded-lg text-sm font-medium hover:bg-gray-200">
                <XMarkIcon className="w-4 h-4" />
                <span>Cancel</span>
              </button>
            </div>
          )}
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
          <Field label="First Name" icon={UserCircleIcon} field="firstName" />
          <Field label="Last Name" icon={UserCircleIcon} field="lastName" />
          <Field label="Email" icon={EnvelopeIcon} field="email" />
          <Field label="Phone" icon={PhoneIcon} field="phone" />
          <Field label="Batch" icon={AcademicCapIcon} field="batch" />
          <Field label="Department" icon={BuildingOfficeIcon} field="department" />
          <Field label="Target Package (LPA)" icon={BriefcaseIcon} field="targetPackage" type="number" />
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        {[
          { label: 'Progress', value: `${user?.overallProgress || 0}%` },
          { label: 'Study Hours', value: user?.totalStudyHours || 0 },
          { label: 'Quizzes', value: user?.quizzesCompleted || 0 },
          { label: 'Streak', value: `${user?.currentStreak || 0} days` },
        ].map(s => (
          <div key={s.label} className="card text-center">
            <p className="text-2xl font-bold text-primary-600">{s.value}</p>
            <p className="text-sm text-gray-500 mt-1">{s.label}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Profile;
