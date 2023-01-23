package com.devBlock.grpc.model;


import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter

public class UniversityDetails {
    double acceptance_rate;
    int act_25;
    int sat_75;
    int act_75;
    String institution_name;
    String avg_gpa;
    int id;
    int sat_25;
    String state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniversityDetails)) return false;
        UniversityDetails that = (UniversityDetails) o;
        return Double.compare(that.getAcceptance_rate(), getAcceptance_rate()) == 0 && getAct_25() == that.getAct_25() && getSat_75() == that.getSat_75() && getAct_75() == that.getAct_75() && getId() == that.getId() && getSat_25() == that.getSat_25() && getInstitution_name().equals(that.getInstitution_name()) && getAvg_gpa().equals(that.getAvg_gpa()) && getState().equals(that.getState());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAcceptance_rate(), getAct_25(), getSat_75(), getAct_75(), getInstitution_name(), getAvg_gpa(), getId(), getSat_25(), getState());
    }
}
