package com.devBlock.grpc.Service;

import com.devBlock.grpc.model.UniversityDetails;
import com.devProblems.*;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
public @GrpcService @Slf4j class StudentServerServices extends StudentServiceGrpc.StudentServiceImplBase {
    @Autowired RestTemplate restTemplate;
    @Value("${open-data-maker-api}")
    private  String openDataApi;
    @Value("${uniFinder-api}")
    private  String apiUrl;
    @Scheduled( cron = "***1****")
    @SneakyThrows
    public @Override  void getUniversityDetail(StudentInfo request,
                                               StreamObserver<UniFinderResponse> responseObserver) {
        String url = apiUrl  + request.getInstitutionId() ;
        UniversityDetails response =
                restTemplate.getForObject(url, UniversityDetails.class);
         String scoreCardApi = "https://api.data.gov/ed/collegescorecard/v1/schools.json?school.name=" + response.getInstitution_name() + openDataApi;
         ResponseEntity<String > scoreApi = restTemplate.getForEntity(scoreCardApi, String.class);
         JSONObject newJsonObject = new JSONObject(scoreApi.getBody().toString());

       try{
           JSONArray resultObject = newJsonObject.getJSONArray("results");
           JSONObject latestObject = resultObject.getJSONObject(0).getJSONObject("latest");
           JSONObject schoolObject = latestObject.getJSONObject("school");
           String schoolZipCode = schoolObject.getString("zip");
           String schoolWebsite = schoolObject.getString("school_url");

           JSONObject cost = latestObject.getJSONObject("cost");
           JSONObject roomboard = cost.getJSONObject("roomboard");
           JSONObject tuition = cost.getJSONObject("tuition");
           String in_state = tuition.getString("in_state");
           String out_of_state= tuition.getString("out_of_state");
           String oncampus= roomboard.getString("oncampus");
           String offcampus= roomboard.getString("offcampus");

           Expense expense = Expense.newBuilder()
                            .setSchoolUrl(schoolWebsite)
                            .setZip(schoolZipCode)
                           .setYearlyTuitionInState(in_state)
                           .setYearlyTuitionOutOfState(out_of_state)
                            .setYearlyOffCampusAccommodation(offcampus)
                            .setYearlyOnCampusAccommodation(oncampus)
                            .build();
           double Student_Gpa = request.getStudentGpa();
           AcceptanceDetail acceptanceDetail  = AcceptanceDetail.newBuilder()
                   .setAcceptanceRate(response.getAcceptance_rate())
                   .setInstitutionName(response.getInstitution_name())
                   .setAvgGpa(response.getAvg_gpa())
                   .build();
           double responseGpa = Double.parseDouble(response.getAvg_gpa());
          List<UniversityDetails> asListOfUnis = getListofEligibleUniversity(request.getStudentGpa() ,"Minnesota");
            if(response.getAvg_gpa().equals(request.getStudentGpa()) || responseGpa <= request.getStudentGpa()){
               UniFinderResponse Eligible = UniFinderResponse.newBuilder()
                       .setAcceptanceDetail(acceptanceDetail)
                       .setMessage("Congratulation : You are Eligible to Apply for this College")
                       .setExpense(expense)
                       .build();
               responseObserver.onNext(Eligible);
               responseObserver.onCompleted();
           }else{
                List<UniversityInfo> as = new ArrayList<>();
                for ( UniversityDetails universityDetails: asListOfUnis){
                    UniversityInfo universityInfo = UniversityInfo.newBuilder()
                            .setAcceptanceRate(universityDetails.getAcceptance_rate())
                            .setAct25(universityDetails.getAct_25())
                            .setSat75(universityDetails.getSat_75())
                            .setAct75(universityDetails.getAct_75())
                            .setInstitutionName(universityDetails.getInstitution_name())
                            .setAvgGpa(Double.parseDouble(universityDetails.getAvg_gpa()))
                            .setId(universityDetails.getId())
                            .setSat25(universityDetails.getSat_25())
                            .setState(universityDetails.getState())
                            .build();
                    as.add(universityInfo);
                }
               UniFinderResponse NotEligible = UniFinderResponse.newBuilder()
                       .setAcceptanceDetail(acceptanceDetail)
                       .setMessage("You are Not Eligible to Apply for this College. Let us Help you Find A better College")
                       .addAllUniversityInfo(as)
                       .build();
               responseObserver.onNext(NotEligible);
               responseObserver.onCompleted();
           }
       }catch (JSONException e){
           log.error("JSON exception" + e);
       }

    }

    @SneakyThrows
    public List<UniversityDetails> getListofEligibleUniversity(double studentGpa, String State){
        String uri = "https://7kbs42o9td.execute-api.us-east-1.amazonaws.com/institutions";
        UniversityDetails [] universityDetails =
                restTemplate.getForObject(uri, UniversityDetails[].class);
        List<UniversityDetails> aListOfUniversityInfo= Arrays.asList(universityDetails);
        List<UniversityDetails> universityDetail =
                aListOfUniversityInfo.stream()
                .filter(item -> item.getAvg_gpa().equals(String.valueOf(studentGpa)))
                .filter(next -> next.getState().equals(State))
                .limit(3)
                .collect(Collectors.toList());
       return universityDetail;
    }
    //     Converting Json to ProtoBuf
    @SneakyThrows
    public static Struct fromJson(String json) {
        Struct.Builder structBuilder = Struct.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder);
        log.info("Here is the Converted Json" + structBuilder.build());
        return structBuilder.build();
    }


}

