/**
 * Brightloop Subscription Management System — P2 Core Backend utilities.
 *
 * Framework-independent helpers for password policy, role checks,
 * customer-record ownership and audit-event construction.
 * No external packages are required.
 */

export type UserRole = 'ADMIN' | 'STAFF' | 'CUSTOMER';

export interface AuthenticatedActor {
  userId: string;
  role: UserRole;
  /** Required for CUSTOMER actors; ignored for ADMIN and STAFF. */
  customerId?: string;
}

export interface PasswordPolicyResult {
  valid: boolean;
  errors: string[];
}

/**
 * Problem-statement password policy:
 * - more than 8 characters (therefore at least 9)
 * - at least one uppercase letter
 * - at least one lowercase letter
 * - at least one special character
 *
 * A number is not mandatory because the problem statement does not require it.
 */
export function validatePassword(password: string): PasswordPolicyResult {
  const errors: string[] = [];

  if (password.length <= 8) {
    errors.push('Password must contain more than 8 characters.');
  }
  if (!/[A-Z]/.test(password)) {
    errors.push('Password must contain at least one uppercase letter.');
  }
  if (!/[a-z]/.test(password)) {
    errors.push('Password must contain at least one lowercase letter.');
  }
  if (!/[^A-Za-z0-9\s]/.test(password)) {
    errors.push('Password must contain at least one special character.');
  }

  return { valid: errors.length === 0, errors };
}

/** Normalize email before persistence; enforce uniqueness in the database too. */
export function normalizeEmail(email: string): string {
  return email.trim().toLowerCase();
}

export class AuthorizationError extends Error {
  readonly statusCode = 403;

  constructor(message = 'You are not authorized to perform this action.') {
    super(message);
    this.name = 'AuthorizationError';
  }
}

/** Only an administrator may add internal staff members. */
export function canCreateStaff(actor: AuthenticatedActor): boolean {
  return actor.role === 'ADMIN';
}

/** Discount-rule creation is explicitly restricted to the administrator. */
export function canCreateDiscountRule(actor: AuthenticatedActor): boolean {
  return actor.role === 'ADMIN';
}

/** Tax rules are admin-configured; keep tax-rule management admin-only. */
export function canManageTaxRules(actor: AuthenticatedActor): boolean {
  return actor.role === 'ADMIN';
}

/**
 * Admin/staff may access customer records for business operations.
 * A customer may access only records belonging to their own customerId.
 * Fail closed when a customer token has no customerId.
 */
export function canAccessCustomerRecord(
  actor: AuthenticatedActor,
  resourceCustomerId: string,
): boolean {
  if (actor.role === 'ADMIN' || actor.role === 'STAFF') return true;
  return Boolean(actor.customerId) && actor.customerId === resourceCustomerId;
}

export function assertCanAccessCustomerRecord(
  actor: AuthenticatedActor,
  resourceCustomerId: string,
): void {
  if (!canAccessCustomerRecord(actor, resourceCustomerId)) {
    throw new AuthorizationError('You can access only your own customer records.');
  }
}

export function assertCanCreateStaff(actor: AuthenticatedActor): void {
  if (!canCreateStaff(actor)) {
    throw new AuthorizationError('Only an administrator can add staff members.');
  }
}

export function assertCanCreateDiscountRule(actor: AuthenticatedActor): void {
  if (!canCreateDiscountRule(actor)) {
    throw new AuthorizationError('Only an administrator can create discount rules.');
  }
}

export function assertCanManageTaxRules(actor: AuthenticatedActor): void {
  if (!canManageTaxRules(actor)) {
    throw new AuthorizationError('Only an administrator can manage tax rules.');
  }
}

export type AuditAction =
  | 'USER_REGISTERED'
  | 'STAFF_CREATED'
  | 'LOGIN_FAILED'
  | 'PASSWORD_RESET_REQUESTED'
  | 'CUSTOMER_UPDATED'
  | 'ROLE_CHANGED'
  | 'TAX_RULE_CREATED'
  | 'TAX_RULE_UPDATED'
  | 'DISCOUNT_RULE_CREATED'
  | 'DISCOUNT_RULE_UPDATED'
  | 'DISCOUNT_RULE_DEACTIVATED';

export interface AuditEventInput {
  actorUserId?: string;
  action: AuditAction;
  targetType: 'USER' | 'CUSTOMER' | 'TAX_RULE' | 'DISCOUNT_RULE';
  targetId?: string;
  /** Store safe business metadata only. Never include passwords or reset tokens. */
  metadata?: Record<string, string | number | boolean | null>;
}

export interface AuditEvent extends AuditEventInput {
  createdAt: Date;
}

/** Build an audit record for persistence by the calling service. */
export function buildAuditEvent(input: AuditEventInput): AuditEvent {
  return { ...input, createdAt: new Date() };
}
