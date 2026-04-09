import { Modal } from "../../../components/ui/Modal"
import { Button } from "../../../components/ui/Button"
import { FormInput } from "../../../components/ui/FormInput"
import { AlertCircle } from "lucide-react"

export default function BankModal({ isOpen, onClose, bankForm, setBankForm, onAdd }) {
    let isError = false
    let errorMsg = ""

    if (
        !bankForm.bankName ||
        !bankForm.branchName ||
        !bankForm.accountNumber ||
        !bankForm.ifscCode ||
        !bankForm.contactPerson ||
        !bankForm.contactNumber
    ) {
        isError = true
        errorMsg = "All fields are required"
    } else if (bankForm.accountNumber.length < 10) {
        isError = true
        errorMsg = "Account Number must be at least 10 digits"
    } else if (bankForm.contactNumber.length !== 10) {
        isError = true
        errorMsg = "Contact Number must be 10 digits"
    } else if (!/^[A-Z]{4}0[A-Z0-9]{6}$/.test(bankForm.ifscCode)) {
        isError = true
        errorMsg = "Invalid IFSC Code. Must be 11 characters (e.g. SBIN0005943)"
    }

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Add Bank">
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <FormInput
                    label="Bank Name"
                    value={bankForm.bankName}
                    onChange={(e) => {
                        if (/^[a-zA-Z\s]*$/.test(e.target.value)) {
                            setBankForm({ ...bankForm, bankName: e.target.value })
                        }
                    }}
                    required
                />
                <FormInput
                    label="Branch Name"
                    value={bankForm.branchName}
                    onChange={(e) => {
                        if (/^[a-zA-Z\s]*$/.test(e.target.value)) {
                            setBankForm({ ...bankForm, branchName: e.target.value })
                        }
                    }}
                    required
                />

                {/* --- NEW FIELDS --- */}
                <FormInput
                    label="Account Number"
                    value={bankForm.accountNumber}
                    onChange={(e) => {
                        const val = e.target.value
                        if (/^\d*$/.test(val) && val.length <= 16) {
                            setBankForm({ ...bankForm, accountNumber: val })
                        }
                    }}
                    required
                />

                <div className="space-y-1">
                    <label className="text-sm font-medium text-gray-700">Account Type <span className="text-red-500">*</span></label>
                    <select
                        value={bankForm.accountType}
                        onChange={(e) => setBankForm({ ...bankForm, accountType: e.target.value })}
                        className="w-full rounded-md border border-gray-300 p-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    >
                        <option value="SAVINGS">Savings</option>
                        <option value="CURRENT">Current</option>
                        <option value="ESCROW">Escrow</option>
                    </select>
                </div>
                {/* ------------------ */}

                <FormInput
                    label="IFSC Code"
                    value={bankForm.ifscCode}
                    onChange={(e) => {
                        const val = e.target.value.toUpperCase()
                        if (/^[A-Z0-9]*$/.test(val) && val.length <= 11) {
                            setBankForm({ ...bankForm, ifscCode: val })
                        }
                    }}
                    maxLength={11}
                    required
                />
                <FormInput
                    label="Contact Person"
                    value={bankForm.contactPerson}
                    onChange={(e) => {
                        if (/^[a-zA-Z\s]*$/.test(e.target.value)) {
                            setBankForm({ ...bankForm, contactPerson: e.target.value })
                        }
                    }}
                    required
                />
                <FormInput
                    label="Contact Number"
                    value={bankForm.contactNumber}
                    onChange={(e) => {
                        const val = e.target.value
                        if (/^\d*$/.test(val) && val.length <= 10) {
                            setBankForm({ ...bankForm, contactNumber: val })
                        }
                    }}
                    required
                />
            </div>

            {isError && (
                <div className="mt-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm flex items-start gap-2">
                    <AlertCircle size={18} className="text-red-500 mt-0.5 shrink-0" />
                    <div>
                        <span className="font-semibold block">Validation Error</span>
                        <span className="text-red-600">{errorMsg}</span>
                    </div>
                </div>
            )}

            <div className="flex flex-col-reverse sm:flex-row gap-2 justify-end mt-6">
                <Button onClick={onClose} variant="secondary" className="w-full sm:w-auto">Cancel</Button>
                <Button
                    onClick={onAdd}
                    disabled={isError}
                    variant="primary"
                    className="w-full sm:w-auto disabled:opacity-50 disabled:cursor-not-allowed"
                >
                    Add Bank
                </Button>
            </div>
        </Modal>
    )
}