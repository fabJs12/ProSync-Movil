package com.luna.prosync.data.remote

import com.luna.prosync.data.remote.dto.AuthResponse
import com.luna.prosync.data.remote.dto.BoardDto
import com.luna.prosync.data.remote.dto.CommentDto
import com.luna.prosync.data.remote.dto.CreateBoardRequest
import com.luna.prosync.data.remote.dto.CreateCommentRequest
import com.luna.prosync.data.remote.dto.CreateProjectRequest
import com.luna.prosync.data.remote.dto.CreateTaskRequest
import com.luna.prosync.data.remote.dto.CreateUserProjectRequest
import com.luna.prosync.data.remote.dto.DashboardStatsDto
import com.luna.prosync.data.remote.dto.GoogleLoginRequest
import com.luna.prosync.data.remote.dto.InviteMemberRequest
import com.luna.prosync.data.remote.dto.LoginRequest
import com.luna.prosync.data.remote.dto.NotificationDto
import com.luna.prosync.data.remote.dto.PageResponse
import com.luna.prosync.data.remote.dto.ProjectDto
import com.luna.prosync.data.remote.dto.TaskDto
import com.luna.prosync.data.remote.dto.TaskFileDto
import com.luna.prosync.data.remote.dto.UpdateRoleRequest
import com.luna.prosync.data.remote.dto.UpdateTaskRequest
import com.luna.prosync.data.remote.dto.UserDto
import com.luna.prosync.data.remote.dto.UserProjectDto
import com.luna.prosync.data.remote.dto.UserRegisterRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {


    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: UserRegisterRequest): Response<ResponseBody>

    @POST("api/auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): AuthResponse

    @GET("api/auth/perfil")
    suspend fun getProfile(): UserDto

    @GET("api/dashboard/stats")
    suspend fun getDashboardStats(): DashboardStatsDto

    @GET("api/projects/listar")
    suspend fun getProjects(): List<ProjectDto>

    @POST("api/projects/crear")
    suspend fun createProject(@Body request: CreateProjectRequest): ProjectDto

    @GET("api/boards/project/{projectId}")
    suspend fun getBoardsByProject(@Path("projectId") projectId: Int): List<BoardDto>

    @POST("api/boards/project/{projectId}")
    suspend fun createBoard(
        @Path("projectId") projectId: Int,
        @Body request: CreateBoardRequest
    ): BoardDto

    @GET("api/tareas/board/{boardId}")
    suspend fun getTasksByBoard(@Path("boardId") boardId: Int): List<TaskDto>

    @POST("api/tareas")
    suspend fun createTask(
        @Body task: CreateTaskRequest
    ): TaskDto

    @GET("api/tareas/{id}")
    suspend fun getTaskById(@Path("id") id: Int): TaskDto

    @PUT("api/tareas/{id}")
    suspend fun updateTask(
        @Path("id") id: Int,
        @Body task: UpdateTaskRequest
    ): TaskDto

    @GET("api/tareas/usuario")
    suspend fun getMyTasks(): List<TaskDto>

    @GET("api/user-projects/project/{projectId}")
    suspend fun getProjectMembers(@Path("projectId") projectId: Int): List<UserProjectDto>

    @GET("api/users/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): UserDto

    @POST("api/user-projects")
    suspend fun addUserToProject(@Body request: CreateUserProjectRequest): UserProjectDto

    @PUT("api/user-projects/{userId}/{projectId}")
    suspend fun updateUserRole(
        @Path("userId") userId: Int,
        @Path("projectId") projectId: Int,
        @Body request: UpdateRoleRequest
    ): UserProjectDto

    @GET("api/notifications")
    suspend fun getUserNotifications(): PageResponse<NotificationDto>

    @PATCH("api/notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: Int): NotificationDto

    @PATCH("api/notifications/read-all")
    suspend fun markAllNotificationsAsRead(): Map<String, Int>

    /* TODO: Endpoints not implemented in API yet
    @POST("api/projects/{projectId}/join")
    suspend fun acceptInvitation(@Path("projectId") projectId: Int): ResponseBody

    @POST("api/projects/{projectId}/invite")
    suspend fun inviteMember(
        @Path("projectId") projectId: Int,
        @Body request: InviteMemberRequest
    ): ResponseBody
    */

    @GET("api/comments/task/{taskId}")
    suspend fun getTaskComments(@Path("taskId") taskId: Int) : List<CommentDto>

    @POST("api/comments")
    suspend fun createComment(@Body request: CreateCommentRequest): CommentDto

    @GET("api/files/task/{taskId}")
    suspend fun getTaskFiles(@Path("taskId") taskId: Int): List<TaskFileDto>

    @Multipart
    @POST("api/files/task/{taskId}")
    suspend fun uploadFile(
        @Path("taskId") taskId: Int,
        @Part file: okhttp3.MultipartBody.Part
    ): retrofit2.Response<TaskFileDto>
}