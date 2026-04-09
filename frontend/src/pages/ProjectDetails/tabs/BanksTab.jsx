import { useState } from "react"
import { Card } from "../../../components/ui/Card"
import { Button } from "../../../components/ui/Button"
import { FormInput } from "../../../components/ui/FormInput"
import { useToast } from "../../../components/ui/Toast"
import { Plus, Edit, Trash2 } from "lucide-react"
import { projectService } from "../../../services/projectService"
import { useAuth } from "../../../contexts/AuthContext"

export default function BanksTab({ banks, projectId, onRefresh }) {
    const { user } = useAuth()
    const { success, error: toastError } = useToast()

    const [isAddingBank, setIsAddingBank] = useState(false)
    const [editingBankId, setEditingBankId] = useState(null)

    const [bankForm, setBankForm] = useState({
        bankName: "",
        branchName: "",
        contactPerson: "",
        contactNumber: "",
        accountNumber: "",
        accountType: "SAVINGS",
        ifscCode: "",
    })

    const handleSaveBank = async () => {
        try {
            if (
                !bankForm.bankName ||
                !bankForm.branchName ||
                !bankForm.accountNumber ||
                !bankForm.ifscCode
            ) {
                toastError("Please fill all required fields")
                return
            }

            if (bankForm.accountNumber.length < 10) {
                toastError("Account Number must be at least 10 digits")
                return
            }

            if (bankForm.contactNumber && bankForm.contactNumber.length !== 10) {
                toastError("Contact Number must be 10 digits")
                return
            }

            if (!/^[A-Z]{4}0[A-Z0-9]{6}$/.test(bankForm.ifscCode)) {
                toastError("Invalid IFSC Code. Must be 11 characters (e.g. SBIN0005943)")
                return
            }

            if (editingBankId) {
                await projectService.updateBankInfo(editingBankId, bankForm)
                success("Bank info updated")
            } else {
                await projectService.createBankInfo(projectId, bankForm)
                success("Bank added")
            }
            setIsAddingBank(false)
            setEditingBankId(null)
            setBankForm({
                bankName: "", branchName: "", contactPerson: "", contactNumber: "",
                accountNumber: "", accountType: "SAVINGS", ifscCode: ""
            })
            if (onRefresh) onRefresh()
        } catch (err) {
            console.error("Error saving bank:", err)
            toastError("Failed to save bank info")
        }
    }

    const handleDeleteBank = async (id) => {
        if (!window.confirm("Delete this bank info?")) return
        try {
            await projectService.deleteBankInfo(id)
            success("Bank deleted")
            if (onRefresh) onRefresh()
        } catch (err) {
            toastError("Failed to delete bank")
        }
    }

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h3 className="text-lg font-semibold">Approved Banks</h3>
                {user?.role === "ADMIN" && (
                    <Button
                        onClick={() => {
                            setEditingBankId(null)
                            setBankForm({
                                bankName: "",
                                branchName: "",
                                contactPerson: "",
                                contactNumber: "",
                                accountNumber: "",
                                accountType: "SAVINGS",
                                ifscCode: "",
                            })
                            setIsAddingBank(true)
                        }}
                    >
                        <Plus size={18} className="mr-2" /> Add Bank
                    </Button>
                )}
            </div>

            {isAddingBank && (
                <Card className="mb-4 bg-gray-50">
                    <h4 className="font-semibold mb-3">
                        {editingBankId ? "Edit Bank Details" : "Add New Bank"}
                    </h4>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                        <FormInput
                            label="Bank Name *"
                            value={bankForm.bankName}
                            onChange={(e) => {
                                const value = e.target.value;
                                if (/^[a-zA-Z\s]*$/.test(value)) {
                                    setBankForm({ ...bankForm, bankName: value });
                                }
                            }}
                        />
                        <FormInput
                            label="Branch Name *"
                            value={bankForm.branchName}
                            onChange={(e) => {
                                const value = e.target.value;
                                if (/^[a-zA-Z\s]*$/.test(value)) {
                                    setBankForm({ ...bankForm, branchName: value });
                                }
                            }}
                        />
                        <FormInput
                            label="Contact Person"
                            value={bankForm.contactPerson}
                            onChange={(e) => {
                                const value = e.target.value;
                                if (/^[a-zA-Z\s]*$/.test(value)) {
                                    setBankForm({ ...bankForm, contactPerson: value });
                                }
                            }}
                        />
                        <FormInput
                            label="Contact Number"
                            value={bankForm.contactNumber}
                            type="text"
                            maxLength={10}
                            onChange={(e) => {
                                const value = e.target.value;
                                if (/^\d{0,10}$/.test(value)) {
                                    setBankForm({ ...bankForm, contactNumber: value });
                                }
                            }}
                        />
                        <FormInput
                            label="Account Number *"
                            value={bankForm.accountNumber}
                            onChange={(e) => {
                                const val = e.target.value
                                if (/^\d*$/.test(val) && val.length <= 16) {
                                    setBankForm({ ...bankForm, accountNumber: val })
                                }
                            }}
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
                        <FormInput
                            label="IFSC Code *"
                            value={bankForm.ifscCode}
                            onChange={(e) => {
                                const val = e.target.value.toUpperCase()
                                if (/^[A-Z0-9]*$/.test(val) && val.length <= 11) {
                                    setBankForm({ ...bankForm, ifscCode: val })
                                }
                            }}
                            maxLength={11}
                        />
                    </div>
                    <div className="flex justify-end gap-2">
                        <Button variant="ghost" onClick={() => setIsAddingBank(false)}>
                            Cancel
                        </Button>
                        <Button onClick={handleSaveBank}>
                            {editingBankId ? "Update Bank" : "Save Bank"}
                        </Button>
                    </div>
                </Card>
            )}

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {banks && banks.length > 0 ? (
                    banks.map((bank) => (
                        <Card key={bank.bankProjectId || bank.id}>
                            <div className="flex justify-between items-start mb-2">
                                <h4 className="font-bold text-lg">{bank.bankName}</h4>
                                {user?.role === "ADMIN" && (
                                    <div className="flex gap-1">
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onClick={() => {
                                                const idToEdit = bank.bankProjectId
                                                if (!idToEdit) {
                                                    toastError("Error: Bank ID missing")
                                                    return
                                                }
                                                setEditingBankId(idToEdit)
                                                setBankForm({
                                                    bankName: bank.bankName,
                                                    branchName: bank.branchName,
                                                    contactPerson: bank.contactPerson || "",
                                                    contactNumber: bank.contactNumber || "",
                                                    accountNumber: bank.accountNumber || "",
                                                    accountType: bank.accountType || "SAVINGS",
                                                    ifscCode: bank.ifscCode || "",
                                                })
                                                setIsAddingBank(true)
                                            }}
                                        >
                                            <Edit size={16} />
                                        </Button>
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            className="text-red-500 hover:text-red-700"
                                            onClick={() => handleDeleteBank(bank.bankProjectId)}
                                        >
                                            <Trash2 size={16} />
                                        </Button>
                                    </div>
                                )}
                            </div>
                            <p className="text-sm text-gray-600">{bank.branchName}</p>
                            <div className="mt-3 pt-3 border-t border-gray-100 text-sm">
                                <p>
                                    <span className="font-medium">A/C No:</span> {bank.accountNumber || "N/A"}
                                </p>
                                <p>
                                    <span className="font-medium">IFSC:</span> {bank.ifscCode || "N/A"}
                                </p>
                                <p>
                                    <span className="font-medium">Type:</span> {bank.accountType || "N/A"}
                                </p>
                            </div>
                            <div className="mt-2 pt-2 border-t border-gray-100 text-sm text-gray-500">
                                <p>
                                    Person: {bank.contactPerson || "N/A"}
                                </p>
                                <p>
                                    Phone: {bank.contactNumber || "N/A"}
                                </p>
                            </div>
                        </Card>
                    ))
                ) : (
                    <div className="col-span-full text-center py-8 text-gray-500 bg-gray-50 rounded-lg border border-dashed border-gray-300">
                        No banks added yet.
                    </div>
                )}
            </div>
        </div>
    )
}