import { FormInput } from "../../../components/ui/FormInput"
import { useToast } from "../../../components/ui/Toast"
import { FormSelect } from "../../../components/ui/FormSelect"
import { FormTextarea } from "../../../components/ui/FormTextarea"

export default function StepBasicInfo({ basicInfo, setBasicInfo }) {
    const { error } = useToast()

    const handleStartDateChange = (e) => {
        const newStartDate = e.target.value
        setBasicInfo((prev) => {
            const updated = { ...prev, startDate: newStartDate }
            if (prev.completionDate && new Date(newStartDate) >= new Date(prev.completionDate)) {
                updated.completionDate = ""
                error("Start date must be before completion date. Completion date has been reset.")
            }
            return updated
        })
    }

    const handleCompletionDateChange = (e) => {
        const newCompletionDate = e.target.value
        if (basicInfo.startDate && new Date(newCompletionDate) <= new Date(basicInfo.startDate)) {
            error("Completion date must be after start date")
            return
        }
        setBasicInfo({ ...basicInfo, completionDate: newCompletionDate })
    }

    return (
        <div className="space-y-4">
            <h2 className="text-xl md:text-2xl font-semibold text-gray-900">Basic Information</h2>
            <FormInput
                label="Project Name"
                value={basicInfo.projectName}
                onChange={(e) => setBasicInfo({ ...basicInfo, projectName: e.target.value })}
                required
            />
            <FormInput
                label="Maharera Number"
                value={basicInfo.mahareraNo}
                onChange={(e) => setBasicInfo({ ...basicInfo, mahareraNo: e.target.value })}
                placeholder="P52100012345"
                required
            />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <FormInput
                    label="Start Date"
                    type="date"
                    value={basicInfo.startDate}
                    min={new Date().toISOString().split("T")[0]}
                    onChange={handleStartDateChange}
                    required
                />
                <FormInput
                    label="Completion Date"
                    type="date"
                    value={basicInfo.completionDate}
                    min={
                        basicInfo.startDate
                            ? new Date(new Date(basicInfo.startDate).getTime() + 86400000).toISOString().split("T")[0]
                            : new Date().toISOString().split("T")[0]
                    }
                    onChange={handleCompletionDateChange}
                    required
                />
            </div>
            <FormSelect
                label="Status"
                value={basicInfo.status}
                onChange={(e) => setBasicInfo({ ...basicInfo, status: e.target.value })}
                options={[
                    { value: "UPCOMING", label: "Upcoming" },
                    { value: "IN_PROGRESS", label: "In Progress" },
                    { value: "COMPLETED", label: "Completed" },
                ]}
            />
            <FormTextarea
                label="Address"
                value={basicInfo.address}
                onChange={(e) => setBasicInfo({ ...basicInfo, address: e.target.value })}
            />
        </div>
    )
}